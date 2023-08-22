package com.reason.ide.search.reference;

import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
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
import it.unimi.dsi.fastutil.ints.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

/**
 * File A : module A1 = {}
 * File B : include A; include A1
 * <p>
 * QNames fullResolution partial include/open names to their real target
 * gist(B) : «A» -> "A" / «A1» -> "A.A1"
 */
public class ORModuleResolutionPsiGist {
    private static final Log LOG = Log.create("gist");
    private static final int VERSION = 1;
    private static final String ID = "reasonml.gist.openincludeqnames";
    private static final Key<RPsiQualifiedPathElement> RESOLUTION = Key.create(ID);
    private static final Key<Integer> ELEMENT_INDEX = Key.create("reasonml.gist.elementindex");
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
            return visitor.getResult();
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
        int myCurrentIndex = 0;

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

            // Compute index during scan
            if (type == myTypes.C_MODULE_DECLARATION || type == myTypes.C_FUNCTOR_DECLARATION ||
                    type == myTypes.C_INCLUDE || type == myTypes.C_OPEN || type == myTypes.C_TAG_START) {
                myCurrentIndex++;
            }

            // MODULE
            // ------
            if (type == myTypes.C_MODULE_DECLARATION) {
                RPsiInnerModule visitedModule = (RPsiInnerModule) element;
                boolean found = false;

                String alias = visitedModule.getAlias();
                if (alias == null) {
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
                                    element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                    myModulesInContext.add(element);
                                    myResult.addValue(myCurrentIndex, functorInContext.getQualifiedName());

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
                                            element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                            myModulesInContext.add(element);
                                            myResult.addValue(myCurrentIndex, moduleQName);

                                            break;
                                        }
                                    }
                                }
                            }

                        }

                        // If nothing found, try direct access
                        if (!found) {
                            // Get functor call path
                            String longIdent = visitedModule.getBody() == null ? "" : ORUtil.getLongIdent(visitedModule.getBody().getFirstChild());
                            String qName = longIdent + functorCall.getName();
                            for (RPsiModule module : ModuleFqnIndex.getElements(qName, myProject, myScope)) {
                                String moduleQName = module.getQualifiedName();
                                if (moduleQName != null) {
                                    element.putUserData(RESOLUTION, module);
                                    element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                    myModulesInContext.add(element);
                                    myResult.addValue(myCurrentIndex, moduleQName);

                                    break;
                                }
                            }
                        }
                    } else {
                        RPsiModuleSignature moduleType = visitedModule.getModuleSignature();
                        if (moduleType != null) {
                            visitModuleResultType(moduleType);
                        } else {
                            visitedModule.putUserData(ELEMENT_INDEX, myCurrentIndex);
                        }
                    }
                } else {
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
                                element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                myModulesInContext.add(element);
                                myResult.addValue(myCurrentIndex, moduleInContext.getQualifiedName());

                                break;
                            } else {
                                String[] path = alias.split("\\.");
                                // alias(path) to a local aliased module (alias[0] == module.name)
                                //   module A = X.Y; module B = A.Z;
                                if (path[0].equals(moduleInContextName)) {
                                    RPsiQualifiedPathElement resolvedModule = moduleInContext.getUserData(RESOLUTION);
                                    String resolvedModuleQName = resolvedModule == null ? "" : resolvedModule.getQualifiedName();
                                    String newAlias = alias.replace(moduleInContextName, resolvedModuleQName == null ? "" : resolvedModuleQName);
                                    Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(newAlias, myProject, myScope);
                                    if (!psiModules.isEmpty()) {
                                        RPsiModule finalModule = psiModules.iterator().next();
                                        String moduleQName = finalModule == null ? null : finalModule.getQualifiedName();
                                        if (moduleQName != null) {
                                            found = true;
                                            element.putUserData(RESOLUTION, finalModule);
                                            element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                            myModulesInContext.add(element);
                                            myResult.addValue(myCurrentIndex, moduleQName);

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
                                        element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                        myModulesInContext.add(element);
                                        myResult.addValue(myCurrentIndex, moduleQName);

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // If nothing found, try direct access
                    if (!found) {
                        for (RPsiModule module : ModuleIndexService.getService().getModules(alias, myProject, myScope)) {
                            String moduleQName = module.getQualifiedName();
                            if (moduleQName != null) {
                                element.putUserData(RESOLUTION, module);
                                element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                myModulesInContext.add(element);
                                myResult.addValue(myCurrentIndex, moduleQName);

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
            // FUNCTOR
            // -------
            else if (type == myTypes.C_FUNCTOR_DECLARATION) {
                RPsiFunctor functor = (RPsiFunctor) element;
                RPsiFunctorResult returnType = functor.getReturnType();
                RPsiUpperSymbol moduleType = returnType == null ? null : returnType.getModuleType();
                if (moduleType == null) {
                    myModulesInContext.add(element);
                    myResult.addValue(myCurrentIndex, functor.getQualifiedName());
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
                                element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                myModulesInContext.add(element);
                                myResult.addValue(myCurrentIndex, resolvedQName);

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
                                        element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                        myModulesInContext.add(element);
                                        myResult.addValue(myCurrentIndex, resolvedModuleQName);

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
                                element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                myModulesInContext.add(element);
                                myResult.addValue(myCurrentIndex, moduleQName);

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
                            element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                            myModulesInContext.add(element);

                            String resolvedQName = finalResolution.getQualifiedName();
                            myResult.addValue(myCurrentIndex, resolvedQName);

                            // File A: include X  -->  A === X
                            if (topLevel && isInclude) {
                                myResult.addValue(0, resolvedQName);
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
                                    element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                    myModulesInContext.add(element);
                                    myResult.addValue(myCurrentIndex, resolvedModuleQName);

                                    // File A: include X  -->  A === X
                                    if (topLevel && isInclude) {
                                        myResult.addValue(0, resolvedModuleQName);
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
                            element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                            myModulesInContext.add(element);

                            String resolvedQName = finalResolution.getQualifiedName();
                            myResult.addValue(myCurrentIndex, resolvedQName);

                            // File A: include X  -->  A === X
                            if (topLevel && isInclude) {
                                myResult.addValue(0, resolvedQName);
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
                                    element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                    myModulesInContext.add(element);

                                    // File A: include A1; include A2  -->  A2 === A1.A2
                                    myResult.addValue(myCurrentIndex, moduleQName);

                                    // File A: include X  -->  A === X
                                    if (topLevel && isInclude) {
                                        myResult.addValue(0, moduleQName);
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }

                // If nothing found, try direct access
                if (!found) {
                    for (RPsiModule module : ModuleIndexService.getService().getModules(visitedPath, myProject, myScope)) {
                        String moduleQName = module.getQualifiedName();
                        if (moduleQName != null) {
                            element.putUserData(RESOLUTION, module);
                            element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                            myModulesInContext.add(element);

                            // File A: include X  -->  A === X
                            if (topLevel && isInclude) {
                                myResult.addValue(0, moduleQName);
                            }

                            if (!moduleQName.equals(visitedPath)) {
                                // File A: open B; include B2  -->  B2 === B.B2
                                myResult.addValue(myCurrentIndex, moduleQName);
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
                                element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                myModulesInContext.add(element);
                                myResult.addValue(myCurrentIndex, componentInContext.getQualifiedName());

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
                                    element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                    myModulesInContext.add(element);
                                    myResult.addValue(myCurrentIndex, moduleQName);
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
                                element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                myModulesInContext.add(element);
                                myResult.addValue(myCurrentIndex, moduleQName);
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
                        RPsiModule resolvedModule = moduleInContext;
                        // If it is a path, must resolve it
                        if (!elementName.equals(elementLongIdent)) {
                            Collection<RPsiModule> modules = ModuleFqnIndex.getElements(elementLongIdent, myProject, myScope);
                            if (modules.size() == 1) {
                                resolvedModule = modules.iterator().next();
                            } else {
                                resolvedModule = null;
                            }
                        }

                        if (resolvedModule != null) {
                            element.putUserData(RESOLUTION, resolvedModule);
                            element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                            myResult.addValue(myCurrentIndex, resolvedModule.getQualifiedName());
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
                                element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                                myResult.addValue(myCurrentIndex, moduleQName);
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
                    element.putUserData(ELEMENT_INDEX, myCurrentIndex);
                    myResult.addValue(myCurrentIndex, moduleQName);
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
                    Integer moduleIndex = module.getUserData(ELEMENT_INDEX);
                    if (moduleIndex != null) {
                        for (RPsiInclude moduleInclude : includes) {
                            Collection<String> includeResolutions = myResult.getValues(moduleInclude);
                            if (includeResolutions.isEmpty()) {
                                myResult.addValue(moduleIndex, moduleInclude.getIncludePath());
                            } else {
                                myResult.addValues(moduleIndex, includeResolutions);
                            }
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

    public static class Data extends Int2ObjectOpenHashMap<Collection<String>> {
        public Data() {
            super();
        }

        public Data(int expectedSize) {
            super(expectedSize);
        }

        public @NotNull Collection<String> getValues(@Nullable PsiElement element) {
            int index = -1;
            if (element instanceof FileBase) {
                index = 0;
            } else if (element != null) {
                Integer elementIndex = element.getUserData(ELEMENT_INDEX);
                index = elementIndex == null ? -1 : elementIndex;
            }

            Collection<String> values = index < 0 ? null : get(index);
            return values == null ? Collections.emptyList() : values;
        }

        public void addValue(Integer key, String value) {
            if (key >= 0) {
                Collection<String> keyValues = computeIfAbsent(key, k -> new TreeSet<>(String::compareTo));
                keyValues.add(value);
            }
        }

        public void addValues(Integer key, Collection<String> values) {
            if (key >= 0) {
                Collection<String> keyValues = computeIfAbsent(key, k -> new TreeSet<>(String::compareTo));
                keyValues.addAll(values);
            }
        }
    }

    public static class Externalizer implements DataExternalizer<Data> {
        @Override
        public void save(@NotNull DataOutput out, Data values) throws IOException {
            DataInputOutputUtil.writeINT(out, values.size());
            for (Int2ObjectMap.Entry<Collection<String>> entry : Int2ObjectMaps.fastIterable(values)) {
                DataInputOutputUtil.writeINT(out, entry.getIntKey());
                DataInputOutputUtil.writeSeq(out, entry.getValue(), out::writeUTF);
            }
        }

        @Override
        public Data read(@NotNull DataInput in) throws IOException {
            int size = DataInputOutputUtil.readINT(in);
            if (size == 0) {
                return new Data();
            }

            Data result = new Data(size);
            for (int i = 0; i < size; i++) {
                int key = DataInputOutputUtil.readINT(in);
                result.put(key, DataInputOutputUtil.readSeq(in, in::readUTF));
            }

            return result;
        }
    }
}
