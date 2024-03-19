package com.reason.ide.search.reference;

import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.gist.*;
import com.intellij.util.io.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

/**
 * File A : module A1 = {}
 * File B : include A; include A1
 * <p>
 * QNames fullResolution partial include/open names to their real target
 * gist(B) : «A» -> "A" / «A1» -> "A.A1"
 */
public class ORModuleResolutionPsiGist {
    private static final Log LOG = Log.create("gist");
    private static final int VERSION = 2;
    private static final String ID = "reasonml.gist.openincludeqnames";
    private static final Key<RPsiQualifiedPathElement> RESOLUTION = Key.create(ID);
    private static final Key<String> ELEMENT_INDEX = Key.create("reasonml.gist.elementindex");
    private static final PsiFileGist<Data> myGist = GistManager.getInstance().newPsiFileGist(ID, VERSION, new ORModuleResolutionPsiGist.Externalizer(), ORModuleResolutionPsiGist::getFileData);

    private ORModuleResolutionPsiGist() {
    }

    public static @NotNull Data getData(@Nullable PsiFile psiFile) {
        // can be from serialisation
        return psiFile == null ? new Data() : myGist.getFileData(psiFile);
    }

    // Resolve all Open and Include paths to their real module definition (ie, remove aliases or intermediate constructions)
    private static Data getFileData(@NotNull PsiFile file) {
        if (file instanceof FileBase) {
            LOG.debug("Walk file to create gist", file);
            PsiWalker visitor = new PsiWalker((FileBase) file);
            file.accept(visitor);
            Data result = visitor.getResult();
            LOG.trace("Gist created for file", file);
            return result;
        }

        return new Data();
    }

    static class PsiWalker extends PsiRecursiveElementWalkingVisitor {
        final Project myProject;
        final GlobalSearchScope myScope;
        final ORLangTypes myTypes;
        final List<PsiElement> myModulesInContext = new ArrayList<>();
        final Map<PsiElement, Integer> myModuleBinding = new HashMap<>();
        final Data myResult;

        public PsiWalker(FileBase file) {
            myTypes = ORTypesUtil.getInstance(file.getLanguage());
            myProject = file.getProject();
            myResult = new Data();
            myModulesInContext.add(file);
            myScope = GlobalSearchScope.allScope(myProject); // ?
        }

        /*
            modules in scope ?
            Belt.Map
            open Belt; Map                        {}:open -> Belt==Belt / {Belt==Belt}:Map -> Map -or- Belt.Map (access to Belt ?) yes
            module B = Belt; B                    {}:alias -> B==Belt / {B==Belt}:B -> B==«B» or B==«Belt» (bc present)
            module B = Belt; module B = {}; B     {}:alias -> B==Belt / {B==Belt}:mod -> B==B / {B==Belt, B==B}:B -> B==«B»
            module Map = { module String = { } }; module Belt = { Map.String }
             */

        @Override
        public void visitElement(@NotNull PsiElement element) {
            super.visitElement(element);
            IElementType type = element.getNode().getElementType();
            ModuleIndexService moduleIndexService = getApplication().getService(ModuleIndexService.class);

            // MODULE
            // ------
            if (type == myTypes.C_MODULE_DECLARATION) {
                RPsiInnerModule visitedModule = (RPsiInnerModule) element;
                boolean found = false;

                String alias = visitedModule.getAlias();
                if (alias == null) {
                    RPsiUnpack unpack = visitedModule.getUnpack();
                    if (unpack != null) {
                        // module M = unpack(firstclass : I)
                        RPsiLowerSymbol firstClassSymbol = unpack.getFirstClassSymbol();
                        String firstClassName = firstClassSymbol != null ? firstClassSymbol.getText() : null;

                        // Iterate backward to find a matching local resolution
                        for (int i = myModulesInContext.size() - 1; i >= 0; i--) {
                            PsiElement elementInContext = myModulesInContext.get(i);
                            if (elementInContext instanceof RPsiFirstClassModule moduleInContext) {
                                String firstClassInContextName = moduleInContext.getElement().getName();

                                // alias to a local module (alias == module.name)
                                //   module A = {}; module B = A;
                                if (firstClassInContextName != null && firstClassInContextName.equals(firstClassName)) {
                                    RPsiQualifiedPathElement userData = moduleInContext.getElement().getUserData(RESOLUTION);
                                    if (userData != null) {
                                        //found = true;
                                        element.putUserData(RESOLUTION, userData);
                                        myModulesInContext.add(element);
                                        myResult.addValue(getIndex(element), userData.getQualifiedName());
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        RPsiFunctorCall functorCall = ORUtil.findImmediateFirstChildOfClass(visitedModule.getBody(), RPsiFunctorCall.class);
                        if (functorCall != null) {
                            // Iterate backward to find a matching local resolution
                            for (int i = myModulesInContext.size() - 1; i >= 0; i--) {
                                PsiElement elementInContext = myModulesInContext.get(i);
                                if (elementInContext instanceof RPsiFunctor functorInContext) { // ? common
                                    String functorInContextName = functorInContext.getModuleName() == null ? "" : functorInContext.getModuleName();

                                    // local functor declaration ?  module F=():S => {}; module M = F({});
                                    if (functorCall.getName().equals(functorInContextName)) {
                                        found = true;

                                        element.putUserData(RESOLUTION, functorInContext);
                                        myModulesInContext.add(element);
                                        myResult.addValue(getIndex(element), functorInContext.getQualifiedName());

                                        break;
                                    }
                                }
                                // Try to combine a previous include/open
                                else if (elementInContext instanceof RPsiInclude || elementInContext instanceof RPsiOpen) {
                                    RPsiQualifiedPathElement moduleInContext = follow(elementInContext);
                                    if (moduleInContext != null) {
                                        String path = ORUtil.getLongIdent(functorCall.getFirstChild());
                                        String pathToTest = moduleInContext.getQualifiedName() + "." + path;
                                        // duplication
                                        Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, myProject, myScope);
                                        if (!psiModules.isEmpty()) {
                                            RPsiModule resolvedModule = psiModules.iterator().next();
                                            String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                                            if (moduleQName != null) {
                                                found = true;
                                                element.putUserData(RESOLUTION, resolvedModule);
                                                myModulesInContext.add(element);
                                                myResult.addValue(getIndex(element), moduleQName);

                                                break;
                                            }
                                        }
                                    }
                                }

                            }

                            // If nothing found, try direct access
                            if (!found) {
                                // Get functor call path
                                String qName = visitedModule.getBody() == null ? functorCall.getName() : ORUtil.getLongIdent(visitedModule.getBody().getFirstChild());
                                for (RPsiModule module : ModuleFqnIndex.getElements(qName, myProject, myScope)) {
                                    String moduleQName = module.getQualifiedName();
                                    if (moduleQName != null) {
                                        element.putUserData(RESOLUTION, module);
                                        myModulesInContext.add(element);
                                        myResult.addValue(getIndex(element), moduleQName);

                                        break;
                                    }
                                }
                            }
                        } else {
                            RPsiModuleSignature moduleType = visitedModule.getModuleSignature();
                            if (moduleType != null) {
                                visitModuleResultType(moduleType);
                            }
                        }
                    }
                } else {
                    String[] aliasPath = alias.split("\\.");

                    // Iterate backward to find a matching local resolution
                    for (int i = myModulesInContext.size() - 1; i >= 0; i--) {
                        PsiElement elementInContext = myModulesInContext.get(i);
                        if (elementInContext instanceof RPsiInnerModule moduleInContext) {
                            String moduleInContextName = moduleInContext.getModuleName() == null ? "" : moduleInContext.getModuleName();

                            // alias to a local module (alias == module.name)
                            //   module A = {}; module B = A;
                            if (alias.equals(moduleInContextName)) {
                                found = true;
                                element.putUserData(RESOLUTION, moduleInContext);
                                myModulesInContext.add(element);
                                myResult.addValue(getIndex(element), moduleInContext.getQualifiedName());

                                break;
                            } else {
                                // alias(path) to a local aliased module (alias[0] == module.name)
                                //   module A = X.Y; module B = A.Z;
                                if (aliasPath[0].equals(moduleInContextName)) {
                                    RPsiQualifiedPathElement resolvedModule = moduleInContext.getUserData(RESOLUTION);
                                    String resolvedModuleQName = resolvedModule == null ? moduleInContext.getQualifiedName() : resolvedModule.getQualifiedName();
                                    String newAlias = alias.replace(moduleInContextName, resolvedModuleQName == null ? "" : resolvedModuleQName);
                                    Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(newAlias, myProject, myScope);
                                    if (!psiModules.isEmpty()) {
                                        RPsiModule finalModule = psiModules.iterator().next();
                                        String moduleQName = finalModule == null ? null : finalModule.getQualifiedName();
                                        if (moduleQName != null) {
                                            found = true;
                                            element.putUserData(RESOLUTION, finalModule);
                                            myModulesInContext.add(element);
                                            myResult.addValue(getIndex(element), moduleQName);

                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // Try to combine a previous include/open
                        //   module A = { module A1 = {} }; module B = A; include B; module C = A1
                        else if (elementInContext instanceof RPsiInclude || elementInContext instanceof RPsiOpen) {
                            RPsiQualifiedPathElement moduleInContext = follow(elementInContext);
                            if (moduleInContext != null) {
                                String pathToTest = moduleInContext.getQualifiedName() + "." + alias;
                                // duplication
                                Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, myProject, myScope);
                                if (!psiModules.isEmpty()) {
                                    RPsiModule resolvedModule = psiModules.iterator().next();
                                    String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                                    if (moduleQName != null) {
                                        found = true;
                                        element.putUserData(RESOLUTION, resolvedModule);
                                        myModulesInContext.add(element);
                                        myResult.addValue(getIndex(element), moduleQName);

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // If nothing found, try direct access
                    if (!found) {
                        for (RPsiModule module : moduleIndexService.getModules(alias, myProject, myScope)) {
                            String moduleQName = module.getQualifiedName();
                            if (moduleQName != null) {
                                element.putUserData(RESOLUTION, module);
                                myModulesInContext.add(element);
                                myResult.addValue(getIndex(element), moduleQName);

                                break;
                            }
                        }
                    }
                }
            }
            // MODULE BINDING
            // --------------
            else if (type == myTypes.C_MODULE_BINDING) {
                myModuleBinding.put(element, myModulesInContext.size());
            }
            // FIRST CLASS MODULE (LET/PARAM)
            // ------------------------
            else if (type == myTypes.C_LET_DECLARATION || type == myTypes.C_PARAM_DECLARATION) {
                RPsiSignatureElement visitedElement = (RPsiSignatureElement) element;
                if (visitedElement.getSignature() instanceof RPsiModuleSignature signature) {
                    String visitedModuleQName = signature.getQName();
                    String[] visitedModulePath = visitedModuleQName.split("\\.");
                    boolean found = false;

                    // Iterate backward to find a matching local resolution
                    for (int i = myModulesInContext.size() - 1; i >= 0; i--) {
                        PsiElement elementInContext = myModulesInContext.get(i);
                        if (elementInContext instanceof RPsiInnerModule moduleInContext) {
                            String moduleInContextName = moduleInContext.getModuleName() == null ? "" : moduleInContext.getModuleName();
                            if (visitedModuleQName.equals(moduleInContextName)) {
                                // first class of a local module
                                found = true;
                                visitedElement.putUserData(RESOLUTION, moduleInContext);
                                break;
                            } else if (visitedModulePath[0].equals(moduleInContextName)) {
                                RPsiQualifiedPathElement resolvedModule = moduleInContext.getUserData(RESOLUTION);
                                String resolvedModuleQName = resolvedModule != null ? resolvedModule.getQualifiedName() : moduleInContext.getQualifiedName();
                                String newModuleName = visitedModuleQName.replace(moduleInContextName, resolvedModuleQName == null ? "" : resolvedModuleQName);
                                Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(newModuleName, myProject, myScope);
                                if (!psiModules.isEmpty()) {
                                    RPsiModule finalModule = psiModules.iterator().next();
                                    String moduleQName = finalModule == null ? null : finalModule.getQualifiedName();
                                    if (moduleQName != null) {
                                        found = true;
                                        visitedElement.putUserData(RESOLUTION, finalModule);
                                        break;
                                    }
                                }
                            }
                        } else if (elementInContext instanceof RPsiInclude || elementInContext instanceof RPsiOpen) {
                            RPsiQualifiedPathElement moduleInContext = follow(elementInContext);
                            if (moduleInContext != null) {
                                String pathToTest = moduleInContext.getQualifiedName() + "." + visitedModuleQName;
                                // duplication
                                Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, myProject, myScope);
                                if (!psiModules.isEmpty()) {
                                    RPsiModule resolvedModule = psiModules.iterator().next();
                                    String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                                    if (moduleQName != null) {
                                        found = true;
                                        visitedElement.putUserData(RESOLUTION, resolvedModule);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // If nothing found, try direct access
                    if (!found) {
                        for (RPsiModule resolvedModule : moduleIndexService.getModules(visitedModuleQName, myProject, myScope)) {
                            String moduleQName = resolvedModule.getQualifiedName();
                            if (moduleQName != null) {
                                visitedElement.putUserData(RESOLUTION, resolvedModule);
                                found = true;
                            }
                        }
                    }

                    if (found && visitedElement instanceof RPsiQualifiedPathElement visitedQualified) {
                        myModulesInContext.add(new RPsiFirstClassModule(visitedQualified));
                    }
                }
            }
            // FUNCTOR
            // -------
            else if (type == myTypes.C_FUNCTOR_DECLARATION) {
                RPsiFunctor functor = (RPsiFunctor) element;
                RPsiFunctorResult returnType = functor.getReturnType();
                RPsiUpperSymbol moduleType = returnType == null ? null : returnType.getModuleType();
                if (moduleType == null) {
                    myModulesInContext.add(element);
                    myResult.addValue(getIndex(element), functor.getQualifiedName());
                } else {
                    // Try to fullResolution type
                    boolean found = false;
                    String returnTypeQName = returnType.getText();

                    for (int i = myModulesInContext.size() - 1; i >= 0; i--) {
                        PsiElement elementInContext = myModulesInContext.get(i);
                        if (elementInContext instanceof RPsiInnerModule moduleInContext) {
                            String moduleInContextName = moduleInContext.getModuleName() == null ? "" : moduleInContext.getModuleName();

                            // local module type (path == module.name)
                            //   module type S = {}; module F = () : S => {}
                            if (moduleType.getText().equals(moduleInContextName)) {
                                RPsiQualifiedPathElement fullModuleResolution = follow(moduleInContext);
                                RPsiQualifiedPathElement finalResolution = fullModuleResolution == null ? moduleInContext : fullModuleResolution;
                                String resolvedQName = finalResolution.getQualifiedName();
                                found = true;

                                element.putUserData(RESOLUTION, finalResolution);
                                myModulesInContext.add(element);
                                myResult.addValue(getIndex(element), resolvedQName);

                                break;
                            } else {
                                String moduleQName = fullResolution(moduleInContext).getQualifiedName();
                                String pathToTest = moduleQName + returnTypeQName.replace(moduleInContextName, "");
                                Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, myProject, myScope);
                                if (!psiModules.isEmpty()) {
                                    RPsiQualifiedPathElement resolvedModule = fullResolution(psiModules.iterator().next());
                                    String resolvedModuleQName = resolvedModule.getQualifiedName();
                                    if (resolvedModuleQName != null) {
                                        found = true;
                                        element.putUserData(RESOLUTION, resolvedModule);
                                        myModulesInContext.add(element);
                                        myResult.addValue(getIndex(element), resolvedModuleQName);

                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // If nothing found, try direct access
                    if (!found) {
                        for (RPsiModule module : ModuleFqnIndex.getElements(moduleType.getText(), myProject, myScope)) {
                            String moduleQName = module.getQualifiedName();
                            if (moduleQName != null) {
                                element.putUserData(RESOLUTION, module);
                                myModulesInContext.add(element);
                                myResult.addValue(getIndex(element), moduleQName);

                                break;
                            }
                        }
                    }
                }
            }
            // INCLUDE/OPEN
            // ------------
            else if (type == myTypes.C_INCLUDE || type == myTypes.C_OPEN) {
                boolean isInclude = type == myTypes.C_INCLUDE;
                String visitedPath = isInclude ? ((RPsiInclude) element).getIncludePath() : ((RPsiOpen) element).getPath();

                boolean found = false;
                boolean topLevel = myModuleBinding.isEmpty();

                // reverse iterate to find resolution
                for (int i = myModulesInContext.size() - 1; i >= 0; i--) {
                    PsiElement elementInContext = myModulesInContext.get(i);
                    if (elementInContext instanceof RPsiInnerModule moduleInContext) {
                        String moduleInContextName = moduleInContext.getModuleName() == null ? "" : moduleInContext.getModuleName();

                        // include/open local module (path == module.name)
                        //   module A = {};
                        //   include/open A;
                        if (visitedPath.equals(moduleInContextName)) {
                            RPsiQualifiedPathElement fullModuleResolution = follow(moduleInContext);
                            RPsiQualifiedPathElement finalResolution = fullModuleResolution == null ? moduleInContext : fullModuleResolution;
                            found = true;

                            element.putUserData(RESOLUTION, finalResolution);
                            myModulesInContext.add(element);

                            String resolvedQName = finalResolution.getQualifiedName();
                            myResult.addValue(getIndex(element), resolvedQName);

                            // File A: include X  -->  A === X
                            if (topLevel && isInclude) {
                                myResult.addValue("", resolvedQName);
                            }

                            break;
                        }
                        // try to fullResolution from local module.
                        //   module A = {}; «OR» module A = B;
                        //   include/open A.A1
                        else {
                            String moduleQName = fullResolution(moduleInContext).getQualifiedName();
                            String pathToTest = moduleQName + visitedPath.replace(moduleInContextName, "");
                            Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, myProject, myScope);
                            if (!psiModules.isEmpty()) {
                                RPsiQualifiedPathElement resolvedModule = fullResolution(psiModules.iterator().next());
                                String resolvedModuleQName = resolvedModule.getQualifiedName();
                                if (resolvedModuleQName != null) {
                                    found = true;
                                    element.putUserData(RESOLUTION, resolvedModule);
                                    myModulesInContext.add(element);
                                    myResult.addValue(getIndex(element), resolvedModuleQName);

                                    // File A: include X  -->  A === X
                                    if (topLevel && isInclude) {
                                        myResult.addValue("", resolvedModuleQName);
                                    }

                                    break;
                                }
                            }
                        }
                    }
                    //
                    else if (elementInContext instanceof RPsiFunctor functorInContext) {
                        String functorInContextName = functorInContext.getModuleName() == null ? "" : functorInContext.getModuleName();

                        // include/open a local functor (path == module.name)
                        //   module A = {};
                        //   include/open A;
                        if (visitedPath.equals(functorInContextName)) {
                            RPsiQualifiedPathElement fullModuleResolution = follow(functorInContext);
                            RPsiQualifiedPathElement finalResolution = fullModuleResolution == null ? functorInContext : fullModuleResolution;
                            found = true;

                            element.putUserData(RESOLUTION, finalResolution);
                            myModulesInContext.add(element);

                            String resolvedQName = finalResolution.getQualifiedName();
                            myResult.addValue(getIndex(element), resolvedQName);

                            // File A: include X  -->  A === X
                            if (topLevel && isInclude) {
                                myResult.addValue("", resolvedQName);
                            }

                            break;
                        }

                    }
                    // Try to combine a previous include/open
                    //   module A = { module A1 = {} }
                    //   include A
                    //   include A1
                    else if (elementInContext instanceof RPsiInclude || elementInContext instanceof RPsiOpen) {
                        RPsiQualifiedPathElement moduleInContext = follow(elementInContext);
                        if (moduleInContext != null) {
                            // include »X«; include Y;
                            String pathToTest = moduleInContext.getQualifiedName() + "." + visitedPath;
                            // duplication
                            Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, myProject, myScope);
                            if (!psiModules.isEmpty()) {
                                RPsiModule resolvedModule = psiModules.iterator().next();
                                String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                                if (moduleQName != null) {
                                    found = true;
                                    element.putUserData(RESOLUTION, resolvedModule);
                                    myModulesInContext.add(element);

                                    // File A: include A1; include A2  -->  A2 === A1.A2
                                    myResult.addValue(getIndex(element), moduleQName);

                                    // File A: include X  -->  A === X
                                    if (topLevel && isInclude) {
                                        myResult.addValue("", moduleQName);
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }

                // If nothing found, try direct access
                if (!found) {
                    for (RPsiModule module : moduleIndexService.getModules(visitedPath, myProject, myScope)) {
                        String moduleQName = module.getQualifiedName();
                        if (moduleQName != null) {
                            element.putUserData(RESOLUTION, module);
                            myModulesInContext.add(element);

                            // File A: include X  -->  A === X
                            if (topLevel && isInclude) {
                                myResult.addValue("", moduleQName);
                            }

                            if (!moduleQName.equals(visitedPath)) {
                                // File A: open B; include B2  -->  B2 === B.B2
                                myResult.addValue(getIndex(element), moduleQName);
                            }
                            break;
                        }
                    }
                }
            }
            // TAG
            // ---
            else if (type == myTypes.C_TAG_START) {
                boolean found = false;
                RPsiTagStart tagFound = ((RPsiTagStart) element);
                String tagName = tagFound.getName();
                if (tagName != null) {
                    // Iterate backward to find a matching local resolution
                    for (int i = myModulesInContext.size() - 1; i >= 0; i--) {
                        PsiElement elementInContext = myModulesInContext.get(i);
                        if (elementInContext instanceof RPsiInnerModule componentInContext && ((RPsiInnerModule) elementInContext).isComponent()) {
                            String componentInContextName = componentInContext.getModuleName();

                            // local component declaration
                            if (tagName.equals(componentInContextName)) {
                                found = true;

                                element.putUserData(RESOLUTION, componentInContext);
                                myModulesInContext.add(element);
                                myResult.addValue(getIndex(element), componentInContext.getQualifiedName());

                                break;
                            }
                        } else if (elementInContext instanceof RPsiOpen openInContext) {
                            RPsiQualifiedPathElement resolvedOpen = openInContext.getUserData(RESOLUTION);
                            String pathToTest = (resolvedOpen == null ? "" : resolvedOpen.getQualifiedName() + ".") + tagName + ".make";
                            Collection<RPsiLet> componentFunctions = LetComponentFqnIndex.getElements(pathToTest, myProject, myScope);
                            if (!componentFunctions.isEmpty()) {
                                RPsiLet resolvedLet = componentFunctions.iterator().next();
                                RPsiModule resolvedModule = PsiTreeUtil.getStubOrPsiParentOfType(resolvedLet, RPsiModule.class);
                                String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                                if (moduleQName != null) {
                                    found = true;
                                    element.putUserData(RESOLUTION, resolvedModule);
                                    myModulesInContext.add(element);
                                    myResult.addValue(getIndex(element), moduleQName);
                                    break;
                                }
                            }
                        }
                    }

                    // If nothing found, try direct access
                    if (!found) {
                        for (RPsiLet componentFunction : LetComponentFqnIndex.getElements(tagName + ".make", myProject, myScope)) {
                            RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(componentFunction, RPsiModule.class);
                            String moduleQName = module == null ? null : module.getQualifiedName();
                            if (moduleQName != null) {
                                element.putUserData(RESOLUTION, module);
                                myModulesInContext.add(element);
                                myResult.addValue(getIndex(element), moduleQName);
                                break;
                            }
                        }
                    }
                }
            }
        }

        private void visitModuleResultType(@NotNull RPsiModuleSignature element) {
            PsiElement firstModuleName = ORUtil.findImmediateFirstChildOfType(element, myTypes.A_MODULE_NAME);
            String elementName = firstModuleName != null ? firstModuleName.getText() : "";
            String elementLongIdent = ORUtil.getLongIdent(element);

            // Iterate backward to find a matching local resolution
            for (int i = myModulesInContext.size() - 1; i >= 0; i--) {
                PsiElement elementInContext = myModulesInContext.get(i);

                // local module type declaration ->  module type S = {}; module M: S = {};
                if (elementInContext instanceof RPsiModule moduleInContext) {
                    String moduleInContextName = moduleInContext.getModuleName() != null ? moduleInContext.getModuleName() : "";

                    if (moduleInContextName.equals(elementName)) {
                        RPsiQualifiedPathElement fullModuleResolution = follow(moduleInContext);
                        RPsiQualifiedPathElement resolvedModule = fullModuleResolution != null ? fullModuleResolution : moduleInContext;

                        // If it is a path, must resolve it
                        if (!elementName.equals(elementLongIdent)) {
                            String pathToResolve = resolvedModule.getQualifiedName() + elementLongIdent.replaceFirst(moduleInContextName, "");
                            Collection<RPsiModule> modules = ModuleFqnIndex.getElements(pathToResolve, myProject, myScope);
                            if (modules.size() == 1) {
                                resolvedModule = modules.iterator().next();
                            } else {
                                resolvedModule = null;
                            }
                        }

                        if (resolvedModule != null) {
                            element.putUserData(RESOLUTION, resolvedModule);
                            myResult.addValue(getIndex(element), resolvedModule.getQualifiedName());
                            return;
                        }
                    }
                }

                // Try to combine a previous include/open
                else if (elementInContext instanceof RPsiInclude || elementInContext instanceof RPsiOpen) {
                    RPsiQualifiedPathElement moduleInContext = follow(elementInContext);
                    if (moduleInContext != null) {
                        String pathToTest = moduleInContext.getQualifiedName() + "." + elementLongIdent;
                        // duplication
                        Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, myProject, myScope);
                        if (!psiModules.isEmpty()) {
                            RPsiModule resolvedModule = psiModules.iterator().next();
                            String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                            if (moduleQName != null) {
                                element.putUserData(RESOLUTION, resolvedModule);
                                myResult.addValue(getIndex(element), moduleQName);
                                return;
                            }
                        }
                    }
                }
            }

            // If nothing found, try direct access
            for (RPsiModule module : ModuleFqnIndex.getElements(elementLongIdent, myProject, myScope)) {
                String moduleQName = module.getQualifiedName();
                if (moduleQName != null) {
                    element.putUserData(RESOLUTION, module);
                    myResult.addValue(getIndex(element), moduleQName);
                }
            }
        }

        @Override
        protected void elementFinished(PsiElement element) {
            IElementType type = element.getNode().getElementType();
            if (type == myTypes.C_MODULE_DECLARATION) {
                RPsiInnerModule module = (RPsiInnerModule) element;
                myModulesInContext.add(module);
                // If there are any (top-binding) includes in the module, they are equivalent
                List<RPsiInclude> includes = ORUtil.findImmediateChildrenOfClass(module.getBody(), RPsiInclude.class);
                if (!includes.isEmpty()) {
                    for (RPsiInclude moduleInclude : includes) {
                        Collection<String> includeResolutions = myResult.getValues(moduleInclude);
                        if (includeResolutions.isEmpty()) {
                            myResult.addValue(getIndex(module), moduleInclude.getIncludePath());
                        } else {
                            myResult.addValues(getIndex(module), includeResolutions);
                        }
                    }
                }
            } else if (type == myTypes.C_MODULE_BINDING) {
                Integer bindingPos = myModuleBinding.remove(element);
                if (myModulesInContext.size() > bindingPos) {
                    myModulesInContext.subList(bindingPos, myModulesInContext.size()).clear();
                }
            }
        }

        private @NotNull RPsiQualifiedPathElement fullResolution(@NotNull RPsiModule module) {
            RPsiQualifiedPathElement resolved = follow(module);
            return resolved == null ? module : resolved;
        }

        private @Nullable RPsiQualifiedPathElement follow(@Nullable PsiElement elementInContext) {
            RPsiQualifiedPathElement resolvedElement = elementInContext == null ? null : elementInContext.getUserData(RESOLUTION);
            if (resolvedElement != null) {
                RPsiQualifiedPathElement alternateResolution = follow(resolvedElement);
                if (alternateResolution != null) {
                    resolvedElement = alternateResolution;
                }
            }
            return resolvedElement;
        }

        public Data getResult() {
            return myResult;
        }
    }


    public static @NotNull String getIndex(@NotNull PsiElement element) {
        if (element instanceof PsiFile) {
            return "";
        }

        String index = element.getUserData(ELEMENT_INDEX);
        if (index != null) {
            return index;
        }

        ORLangTypes types = ORUtil.getTypes(element.getLanguage());
        int count = 0;

        PsiElement prevSibling = element.getPrevSibling();
        IElementType prevElementType = prevSibling != null ? prevSibling.getNode().getElementType() : null;
        while (prevElementType != null) {
            if (prevElementType == types.C_MODULE_DECLARATION || prevElementType == types.C_FUNCTOR_DECLARATION ||
                    prevElementType == types.C_INCLUDE || prevElementType == types.C_OPEN || prevElementType == types.C_TAG_START) {
                count++;
            }
            prevSibling = prevSibling.getPrevSibling();
            prevElementType = prevSibling != null ? prevSibling.getNode().getElementType() : null;
        }

        PsiElement parent = element.getParent();
        String parentIndex = parent != null ? getIndex(parent) : "";

        String result = (parentIndex.isEmpty() ? "" : parentIndex + ".") + count;
        element.putUserData(ELEMENT_INDEX, result);

        return result;
    }

    public static class Data {
        private final Map<String, Collection<String>> items = new HashMap<>();

        public @NotNull Collection<String> getValues(@Nullable PsiElement element) {
            Collection<String> values = null;
            if (element != null) {
                values = items.get(getIndex(element));
            }
            return values != null ? values : Collections.emptyList();
        }

        public void addValue(@NotNull String key, @Nullable String value) {
            if (value != null) {
                Collection<String> keyValues = items.computeIfAbsent(key, k -> new ArrayList<>());
                keyValues.add(value);
            }
        }

        public void addValues(@NotNull String key, @NotNull Collection<String> values) {
            Collection<String> keyValues = items.computeIfAbsent(key, k -> new TreeSet<>(String::compareTo));
            keyValues.addAll(values);
        }

        public int size() {
            return items.size();
        }
    }

    public static class Externalizer implements DataExternalizer<Data> {
        @Override
        public void save(@NotNull DataOutput out, Data values) throws IOException {
            DataInputOutputUtil.writeINT(out, values.size());
            for (Map.Entry<String, Collection<String>> entry : values.items.entrySet()) {
                out.writeUTF(entry.getKey());
                DataInputOutputUtil.writeSeq(out, entry.getValue(), out::writeUTF);
            }
        }

        @Override
        public Data read(@NotNull DataInput in) throws IOException {
            int size = DataInputOutputUtil.readINT(in);
            Data result = new Data();
            for (int i = 0; i < size; i++) {
                String key = in.readUTF();
                result.addValues(key, DataInputOutputUtil.readSeq(in, in::readUTF));
            }

            return result;
        }
    }

    static class RPsiFirstClassModule extends FakePsiElement {
        private final RPsiQualifiedPathElement myElement;

        public RPsiFirstClassModule(RPsiQualifiedPathElement element) {
            myElement = element;
        }

        public RPsiQualifiedPathElement getElement() {
            return myElement;
        }

        @Override
        public @Nullable PsiElement getParent() {
            return myElement.getParent();
        }

        @Override
        public @Nullable String getText() {
            return myElement.getText();
        }

        @Override
        public @NotNull TextRange getTextRangeInParent() {
            return TextRange.EMPTY_RANGE;
        }
    }
}
