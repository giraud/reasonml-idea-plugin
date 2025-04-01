package com.reason.ide.search.reference;

import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.gist.*;
import com.intellij.util.io.*;
import com.reason.ide.*;
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

import static com.intellij.openapi.application.ApplicationManager.*;

/**
 * Resolved aliases and includes in a module.
 * <pre>
 * File A : module A1 = {}
 * File B : include A; include A1
 * </pre>
 * QNames fullResolution partial include/open names to their real target
 * <pre>
 * gist(B) : «A» -> "A" / «A1» -> "A.A1"
 * </pre>
 */
public class ORModuleResolutionPsiGist {
    private static final Log LOG = Log.create("gist");
    private static final int VERSION = 3;
    private static final String ID = "reasonML.gist.openIncludeQNames";
    private static final Key<RPsiQualifiedPathElement> RESOLUTION = Key.create(ID);
    private static final Key<String> ELEMENT_INDEX = Key.create("reasonML.gist.elementIndex");
    private static final PsiFileGist<Data> myGist = GistManager.getInstance()
            .newPsiFileGist(ID, VERSION, new ORModuleResolutionPsiGist.Externalizer(), ORModuleResolutionPsiGist::getFileData);

    private ORModuleResolutionPsiGist() {
    }

    public static @NotNull Data getData(@Nullable PsiFile psiFile) {
        // can be from serialisation
        return psiFile == null ? new Data() : myGist.getFileData(psiFile);
    }

    // Resolve all Open and Include paths to their real module definition (ie, remove aliases or intermediate constructions)
    private static Data getFileData(@NotNull PsiFile file) {
        Data data = new Data();

        if (file instanceof FileBase psiFileBase) {
            Map<String, Data> visitedFiles = new HashMap<>();

            Context context = new Context(getApplication().getService(ModuleIndexService.class), file.getProject(), GlobalSearchScope.allScope(file.getProject()));
            WalkInfo walkInfo = new WalkInfo(psiFileBase);

            long start = System.currentTimeMillis();
            VirtualFile vFile = psiFileBase.getVirtualFile();
            LOG.info("Walk file to create gist: " + psiFileBase.getModuleName() + ", " + (vFile == null ? "<NULL>" : vFile.getPath()));

            data = walkFile(context, walkInfo, visitedFiles);

            long time = System.currentTimeMillis() - start;
            LOG.info("Gist created for " + psiFileBase.getModuleName() + " (" + time + "ms), visited: [" + Joiner.join(", ", visitedFiles.keySet()) + "]");
        }

        return data;
    }


    private static @NotNull Data walkFile(@NotNull Context context, @NotNull WalkInfo walkInfo, Map<String, Data> visitedFiles) {
        if (visitedFiles.containsKey(walkInfo.moduleName)) {
            LOG.trace("  Already visited file", (PsiFile) walkInfo.file);
            return visitedFiles.get(walkInfo.moduleName);
        }

        Data data = new Data();
        visitedFiles.put(walkInfo.moduleName, data);

        PsiElement element = walkInfo.file.getFirstChild();
        PsiElement firstChild = null;
        while (element != null) {
            // MODULE/FUNCTOR
            if (element instanceof RPsiInnerModule visitedModule) {
                visitInnerModule(context, walkInfo, visitedFiles, visitedModule, data);
                firstChild = element.getFirstChild();
            }
            // FUNCTOR
            else if (element instanceof RPsiFunctor visitedFunctor) {
                visitFunctor(context, walkInfo, visitedFunctor, data);
                firstChild = element.getFirstChild();
            }
            // MODULE BINDING
            else if (element instanceof RPsiModuleBinding) {
                walkInfo.moduleBindings.put(element, walkInfo.modulesInContext.size());
                firstChild = element.getFirstChild();
            }
            // INCLUDE/OPEN
            else if (element instanceof RPsiInclude || element instanceof RPsiOpen) {
                visitIncludeOpen(context, walkInfo, element, data);
            }
            // FIRST CLASS MODULE (LET/PARAM)
            else if (element instanceof RPsiVar || element instanceof RPsiParameterDeclaration) {
                visitFirstClassModule(context, walkInfo, (RPsiSignatureElement) element);
                firstChild = element.getFirstChild();
            }
            // TAG
            else if (element instanceof RPsiTagStart visitedTag) {
                visitedTag(context, walkInfo, visitedTag, data);
                firstChild = element.getFirstChild();
            } else {
                firstChild = element.getFirstChild();
            }

            if (firstChild != null) {
                element = firstChild;
                firstChild = null;
            } else {
                PsiElement nextElement = ORUtil.nextSibling(element);
                if (nextElement != null) {
                    element = nextElement;
                } else {
                    // Close elements (go back to parent)
                    PsiElement parent = element.getParent();
                    if (parent == walkInfo.file) {
                        element = null;
                    } else {
                        closeVisitedElement(walkInfo, data, parent);
                        while (parent != null) {
                            element = ORUtil.nextSibling(parent);
                            if (element != null) {
                                parent = null;
                            } else {
                                parent = parent.getParent();
                                if (parent == walkInfo.file) {
                                    parent = null;
                                } else {
                                    closeVisitedElement(walkInfo, data, parent);
                                }
                            }
                        }
                    }
                }
            }
        }

        return data;
    }

    private static void visitedTag(@NotNull Context context, @NotNull WalkInfo walkInfo, @NotNull RPsiTagStart visitedTag, @NotNull Data data) {
        boolean found = false;
        String tagName = visitedTag.getName();
        if (tagName != null) {
            // Iterate backward to find a matching local resolution
            for (int i = walkInfo.modulesInContext.size() - 1; i >= 0; i--) {
                PsiElement elementInContext = walkInfo.modulesInContext.get(i);
                if (elementInContext instanceof RPsiInnerModule componentInContext && componentInContext.isComponent()) {
                    String componentInContextName = componentInContext.getModuleName();

                    // local component declaration
                    if (tagName.equals(componentInContextName)) {
                        found = true;

                        visitedTag.putUserData(RESOLUTION, componentInContext);
                        walkInfo.modulesInContext.add(visitedTag);
                        data.addValue(getIndex(visitedTag), componentInContext.getQualifiedName());

                        break;
                    }
                } else if (elementInContext instanceof RPsiOpen openInContext) {
                    RPsiQualifiedPathElement resolvedOpen = openInContext.getUserData(RESOLUTION);
                    String pathToTest = (resolvedOpen == null ? "" : resolvedOpen.getQualifiedName() + ".") + tagName + ".make";
                    Collection<RPsiLet> componentFunctions = LetComponentFqnIndex.getElements(pathToTest, context.project, context.scope);
                    if (!componentFunctions.isEmpty()) {
                        RPsiLet resolvedLet = componentFunctions.iterator().next();
                        RPsiModule resolvedModule = PsiTreeUtil.getStubOrPsiParentOfType(resolvedLet, RPsiModule.class);
                        String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                        if (moduleQName != null) {
                            found = true;
                            visitedTag.putUserData(RESOLUTION, resolvedModule);
                            walkInfo.modulesInContext.add(visitedTag);
                            data.addValue(getIndex(visitedTag), moduleQName);
                            break;
                        }
                    }
                }
            }

            // If nothing found, try direct access
            if (!found) {
                for (RPsiLet componentFunction : LetComponentFqnIndex.getElements(tagName + ".make", context.project, context.scope)) {
                    RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(componentFunction, RPsiModule.class);
                    String moduleQName = module == null ? null : module.getQualifiedName();
                    if (moduleQName != null) {
                        visitedTag.putUserData(RESOLUTION, module);
                        walkInfo.modulesInContext.add(visitedTag);
                        data.addValue(getIndex(visitedTag), moduleQName);
                        break;
                    }
                }
            }
        }
    }

    private static void visitFunctor(@NotNull Context context, @NotNull WalkInfo walkInfo, RPsiFunctor visitedFunctor, Data data) {
        RPsiFunctorResult returnType = visitedFunctor.getReturnType();
        RPsiUpperSymbol moduleType = returnType == null ? null : returnType.getModuleType();
        if (moduleType == null) {
            walkInfo.modulesInContext.add(visitedFunctor);
            data.addValue(getIndex(visitedFunctor), visitedFunctor.getQualifiedName());
        } else {
            // Try to fullResolution type
            boolean found = false;
            String returnTypeQName = returnType.getText();

            for (int i = walkInfo.modulesInContext.size() - 1; i >= 0; i--) {
                PsiElement elementInContext = walkInfo.modulesInContext.get(i);
                if (elementInContext instanceof RPsiInnerModule moduleInContext) {
                    String moduleInContextName = moduleInContext.getModuleName() == null ? "" : moduleInContext.getModuleName();

                    // local module type (path == module.name)
                    //   module type S = {}; module F = () : S => {}
                    if (moduleType.getText().equals(moduleInContextName)) {
                        String resolvedQName = fullResolution(moduleInContext).getQualifiedName();
                        found = true;

                        visitedFunctor.putUserData(RESOLUTION, fullResolution(moduleInContext));
                        walkInfo.modulesInContext.add(visitedFunctor);
                        data.addValue(getIndex(visitedFunctor), resolvedQName);

                        break;
                    } else {
                        String moduleQName = fullResolution(moduleInContext).getQualifiedName();
                        String pathToTest = moduleQName + returnTypeQName.replace(moduleInContextName, "");
                        Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, context.project, context.scope);
                        if (!psiModules.isEmpty()) {
                            RPsiQualifiedPathElement resolvedModule = fullResolution(psiModules.iterator().next());
                            String resolvedModuleQName = resolvedModule.getQualifiedName();
                            if (resolvedModuleQName != null) {
                                found = true;
                                visitedFunctor.putUserData(RESOLUTION, resolvedModule);
                                walkInfo.modulesInContext.add(visitedFunctor);
                                data.addValue(getIndex(visitedFunctor), resolvedModuleQName);

                                break;
                            }
                        }
                    }
                }
            }
            // If nothing found, try direct access
            if (!found) {
                for (RPsiModule module : ModuleFqnIndex.getElements(moduleType.getText(), context.project, context.scope)) {
                    String moduleQName = module.getQualifiedName();
                    if (moduleQName != null) {
                        visitedFunctor.putUserData(RESOLUTION, module);
                        walkInfo.modulesInContext.add(visitedFunctor);
                        data.addValue(getIndex(visitedFunctor), moduleQName);

                        break;
                    }
                }
            }
        }
    }

    private static void closeVisitedElement(@NotNull WalkInfo walkInfo, @NotNull Data data, PsiElement element) {
        if (element instanceof RPsiFunctor functor) {
            if (functor != walkInfo.modulesInContext.getLast()) {
                walkInfo.modulesInContext.add(functor);
            }
        } else if (element instanceof RPsiInnerModule module) {
            if (module != walkInfo.modulesInContext.getLast()) {
                walkInfo.modulesInContext.add(module);
            }
            // If there are any (top-binding) includes in the module, they are equivalent
            List<RPsiInclude> includes = ORUtil.findImmediateChildrenOfClass(module.getBody(), RPsiInclude.class);
            if (!includes.isEmpty()) {
                for (RPsiInclude moduleInclude : includes) {
                    Collection<String> includeResolutions = data.getValues(moduleInclude);
                    if (includeResolutions.isEmpty()) {
                        data.addValue(getIndex(module), moduleInclude.getIncludePath());
                    } else {
                        data.addValues(getIndex(module), includeResolutions);
                    }
                }
            }
        } else if (element instanceof RPsiModuleBinding moduleBinding) {
            Integer bindingPos = walkInfo.moduleBindings.remove(moduleBinding);
            if (walkInfo.modulesInContext.size() > bindingPos) {
                walkInfo.modulesInContext.subList(bindingPos, walkInfo.modulesInContext.size()).clear();
            }
        }
    }

    private static void visitFirstClassModule(@NotNull Context context, @NotNull WalkInfo walkInfo, @NotNull RPsiSignatureElement visitedElement) {
        if (visitedElement.getSignature() instanceof RPsiModuleSignature signature) {
            String visitedModuleQName = signature.getQName();
            String[] visitedModulePath = visitedModuleQName.split("\\.");
            boolean found = false;

            // Iterate backward to find a matching local resolution
            for (int i = walkInfo.modulesInContext.size() - 1; i >= 0; i--) {
                PsiElement elementInContext = walkInfo.modulesInContext.get(i);
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
                        Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(newModuleName, context.project, context.scope);
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
                    RPsiQualifiedPathElement moduleInContext = follow(elementInContext, 0);
                    if (moduleInContext != null) {
                        String pathToTest = moduleInContext.getQualifiedName() + "." + visitedModuleQName;
                        // duplication
                        Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, context.project, context.scope);
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
                String path = ORFileUtils.getParentPath(visitedElement.getContainingFile());
                RPsiQualifiedPathElement resolvedModule = findGlobalModule(path, visitedModuleQName, context);
                if (resolvedModule != null) {
                    String moduleQName = resolvedModule.getQualifiedName();
                    if (moduleQName != null) {
                        visitedElement.putUserData(RESOLUTION, resolvedModule);
                        found = true;
                    }
                }
            }

            if (found && visitedElement instanceof RPsiQualifiedPathElement visitedQualified) {
                walkInfo.modulesInContext.add(new RPsiFirstClassModule(visitedQualified));
            }
        }
    }

    private static void visitInnerModule(@NotNull Context context, @NotNull WalkInfo walkInfo, @NotNull Map<String, Data> visitedFiles, @NotNull RPsiInnerModule visitedModule, @NotNull Data data) {
        boolean found = false;
        String alias = visitedModule.getAlias();

        if (alias == null) {
            RPsiUnpack unpack = visitedModule.getUnpack();
            if (unpack != null) {
                // First class module
                //   module M = unpack(P : I)
                RPsiLowerSymbol firstClassSymbol = unpack.getFirstClassSymbol();
                String firstClassName = firstClassSymbol != null ? firstClassSymbol.getText() : null;

                // Iterate backward to find a matching local resolution
                for (int i = walkInfo.modulesInContext.size() - 1; i >= 0; i--) {
                    PsiElement elementInContext = walkInfo.modulesInContext.get(i);
                    if (elementInContext instanceof RPsiFirstClassModule moduleInContext) {
                        String firstClassInContextName = moduleInContext.getElement().getName();

                        // alias to a local module (alias == module.name)
                        //   module A = {}; module B = A;
                        if (firstClassInContextName != null && firstClassInContextName.equals(firstClassName)) {
                            RPsiQualifiedPathElement userData = moduleInContext.getElement().getUserData(RESOLUTION);
                            if (userData != null) {
                                visitedModule.putUserData(RESOLUTION, userData);
                                walkInfo.modulesInContext.add(visitedModule);
                                data.addValue(getIndex(visitedModule), userData.getQualifiedName());
                                break;
                            }
                        }
                    }
                }
            } else {
                RPsiFunctorCall functorCall = ORUtil.findImmediateFirstChildOfClass(visitedModule.getBody(), RPsiFunctorCall.class);
                if (functorCall != null) {
                    // Iterate backward to find a matching local resolution
                    for (int i = walkInfo.modulesInContext.size() - 1; i >= 0; i--) {
                        PsiElement elementInContext = walkInfo.modulesInContext.get(i);
                        if (elementInContext instanceof RPsiFunctor functorInContext) { // ? common
                            String functorInContextName = functorInContext.getModuleName() == null ? "" : functorInContext.getModuleName();

                            // local functor declaration ?  module F=():S => {}; module M = F({});
                            if (functorCall.getName().equals(functorInContextName)) {
                                found = true;

                                visitedModule.putUserData(RESOLUTION, functorInContext);
                                walkInfo.modulesInContext.add(visitedModule);
                                data.addValue(getIndex(visitedModule), functorInContext.getQualifiedName());

                                break;
                            }
                        }
                        // Try to combine a previous include/open
                        else if (elementInContext instanceof RPsiInclude || elementInContext instanceof RPsiOpen) {
                            RPsiQualifiedPathElement moduleInContext = follow(elementInContext, 0);
                            if (moduleInContext != null) {
                                String path = ORUtil.getLongIdent(functorCall.getFirstChild());
                                String pathToTest = moduleInContext.getQualifiedName() + "." + path;
                                // duplication
                                Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, context.project, context.scope);
                                if (!psiModules.isEmpty()) {
                                    RPsiModule resolvedModule = psiModules.iterator().next();
                                    String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                                    if (moduleQName != null) {
                                        found = true;
                                        visitedModule.putUserData(RESOLUTION, resolvedModule);
                                        walkInfo.modulesInContext.add(visitedModule);
                                        data.addValue(getIndex(visitedModule), moduleQName);

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
                        ResolvedQName resolvedQName = resolveQName(qName, context, walkInfo, visitedFiles);
                        if (resolvedQName.found) {
                            RPsiQualifiedPathElement resolvedFunctor = resolvedQName.resolvedElement;
                            String functorQName = resolvedFunctor.getQualifiedName();
                            if (functorQName != null) {
                                visitedModule.putUserData(RESOLUTION, resolvedFunctor);
                                walkInfo.modulesInContext.add(visitedModule);
                                data.addValue(getIndex(visitedModule), functorQName);
                            }
                        }
                    }
                } else {
                    RPsiModuleSignature moduleType = visitedModule.getModuleSignature();
                    if (moduleType != null) {
                        visitModuleResultType(context, walkInfo, moduleType, data);
                    }
                }
            }
        } else {
            // Alias
            //   module X = A.B.C
            ResolvedQName resolvedQName = resolveQName(alias, context, walkInfo, visitedFiles);
            RPsiQualifiedPathElement resolvedPart = resolvedQName.resolvedElement;
            if (resolvedPart != null && resolvedQName.found) {
                String index = getIndex(visitedModule);

                visitedModule.putUserData(RESOLUTION, resolvedPart);
                walkInfo.modulesInContext.add(visitedModule);
                data.addValue(index, resolvedPart.getQualifiedName());

                PsiFile resolvedContainingFile = resolvedPart.getContainingFile();
                if (resolvedContainingFile instanceof FileBase resolvedContainingFileBase) {
                    String resolvedFileModuleName = resolvedContainingFileBase.getModuleName();
                    if (!walkInfo.moduleName.equals(resolvedFileModuleName)) {
                        // Maybe resolved module include other modules, and we need to add them as alternate names
                        // We can’t reuse the gist here because of mutual calls and possibility of stack overflow.

                        if (LOG.isTraceEnabled()) {
                            LOG.trace("  Use another GIST [from " + walkInfo.moduleName + "] : " + ORFileUtils.getVirtualFile(resolvedContainingFile)
                                    + ", visited: [" + Joiner.join(", ", visitedFiles.keySet()) + "]");
                        }
                        Data anotherData = walkFile(context, new WalkInfo(resolvedContainingFileBase), visitedFiles);

                        for (String value : anotherData.getValues(resolvedPart)) {
                            data.addValue(index, value);
                        }
                    }
                }
            }
        }
    }

    private static void visitModuleResultType(@NotNull Context context, @NotNull WalkInfo walkInfo, @NotNull RPsiModuleSignature element, @NotNull Data data) {
        PsiElement firstModuleName = ORUtil.findImmediateFirstChildOfType(element, walkInfo.types.A_MODULE_NAME);
        String elementName = firstModuleName != null ? firstModuleName.getText() : "";
        String elementLongIdent = ORUtil.getLongIdent(element);

        // Iterate backward to find a matching local resolution
        for (int i = walkInfo.modulesInContext.size() - 1; i >= 0; i--) {
            PsiElement elementInContext = walkInfo.modulesInContext.get(i);

            // local module type declaration ->  module type S = {}; module M: S = {};
            if (elementInContext instanceof RPsiModule moduleInContext) {
                String moduleInContextName = moduleInContext.getModuleName() != null ? moduleInContext.getModuleName() : "";

                if (moduleInContextName.equals(elementName)) {
                    RPsiQualifiedPathElement fullModuleResolution = follow(moduleInContext, 0);
                    RPsiQualifiedPathElement resolvedModule = fullModuleResolution != null ? fullModuleResolution : moduleInContext;

                    // If it is a path, must resolve it
                    if (!elementName.equals(elementLongIdent)) {
                        String pathToResolve = resolvedModule.getQualifiedName() + elementLongIdent.replaceFirst(moduleInContextName, "");
                        Collection<RPsiModule> modules = ModuleFqnIndex.getElements(pathToResolve, context.project, context.scope);
                        if (modules.size() == 1) {
                            resolvedModule = modules.iterator().next();
                        } else {
                            resolvedModule = null;
                        }
                    }

                    if (resolvedModule != null) {
                        element.putUserData(RESOLUTION, resolvedModule);
                        data.addValue(getIndex(element), resolvedModule.getQualifiedName());
                        return;
                    }
                }
            }

            // Try to combine a previous include/open
            else if (elementInContext instanceof RPsiInclude || elementInContext instanceof RPsiOpen) {
                RPsiQualifiedPathElement moduleInContext = follow(elementInContext, 0);
                if (moduleInContext != null) {
                    String pathToTest = moduleInContext.getQualifiedName() + "." + elementLongIdent;
                    // duplication
                    Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, context.project, context.scope);
                    if (!psiModules.isEmpty()) {
                        RPsiModule resolvedModule = psiModules.iterator().next();
                        String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                        if (moduleQName != null) {
                            element.putUserData(RESOLUTION, resolvedModule);
                            data.addValue(getIndex(element), moduleQName);
                            return;
                        }
                    }
                }
            }
        }

        // If nothing found, try direct access
        for (RPsiModule module : ModuleFqnIndex.getElements(elementLongIdent, context.project, context.scope)) {
            String moduleQName = module.getQualifiedName();
            if (moduleQName != null) {
                element.putUserData(RESOLUTION, module);
                data.addValue(getIndex(element), moduleQName);
            }
        }
    }

    private static void visitIncludeOpen(@NotNull Context context, @NotNull WalkInfo walkInfo, @NotNull PsiElement visitedElement, @NotNull Data data) {
        IElementType type = visitedElement.getNode().getElementType();
        boolean isInclude = type == walkInfo.types.C_INCLUDE;
        String visitedPath = isInclude ? ((RPsiInclude) visitedElement).getIncludePath() : ((RPsiOpen) visitedElement).getPath();

        boolean found = false;
        boolean topLevel = walkInfo.moduleBindings.isEmpty();

        // Reverse iterate to find resolution
        for (int i = walkInfo.modulesInContext.size() - 1; i >= 0; i--) {
            PsiElement elementInContext = walkInfo.modulesInContext.get(i);
            if (elementInContext instanceof RPsiInnerModule moduleInContext) {
                // include/open local module (path == module.name)
                //   module A = ?
                //   include/open A;

                String moduleInContextName = moduleInContext.getModuleName() == null ? "" : moduleInContext.getModuleName();
                if (visitedPath.equals(moduleInContextName)) {
                    RPsiQualifiedPathElement finalResolution = fullResolution(moduleInContext);
                    found = true;

                    visitedElement.putUserData(RESOLUTION, finalResolution);
                    walkInfo.modulesInContext.add(visitedElement);

                    String resolvedQName = finalResolution.getQualifiedName();
                    data.addValue(getIndex(visitedElement), resolvedQName);

                    // File A: include X  -->  A === X
                    if (topLevel && isInclude) {
                        data.addValue("", resolvedQName);
                    }

                    break;
                } else {
                    // try fullResolution from local module.
                    //   module A = {...} «OR» module A = B
                    //   include/open A.A1

                    String moduleQName = fullResolution(moduleInContext).getQualifiedName();
                    String pathToTest = moduleQName + visitedPath.replace(moduleInContextName, "");
                    Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, context.project, context.scope);
                    if (!psiModules.isEmpty()) {
                        RPsiQualifiedPathElement resolvedModule = fullResolution(psiModules.iterator().next());
                        String resolvedModuleQName = resolvedModule.getQualifiedName();
                        if (resolvedModuleQName != null) {
                            found = true;
                            visitedElement.putUserData(RESOLUTION, resolvedModule);
                            walkInfo.modulesInContext.add(visitedElement);
                            data.addValue(getIndex(visitedElement), resolvedModuleQName);

                            // File A: include X  -->  A === X
                            if (topLevel && isInclude) {
                                data.addValue("", resolvedModuleQName);
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
                //   module A = ?
                //   include/open A
                if (visitedPath.equals(functorInContextName)) {
                    RPsiQualifiedPathElement finalResolution = fullResolution(functorInContext);
                    found = true;

                    visitedElement.putUserData(RESOLUTION, finalResolution);
                    walkInfo.modulesInContext.add(visitedElement);

                    String resolvedQName = finalResolution.getQualifiedName();
                    data.addValue(getIndex(visitedElement), resolvedQName);

                    // File A: include X  -->  A === X
                    if (topLevel && isInclude) {
                        data.addValue("", resolvedQName);
                    }

                    break;
                }
            }
            // Try to combine a previous include/open
            //   module A = { module A1 = {} }
            //   include A
            //   include A1
            else if (elementInContext instanceof RPsiInclude || elementInContext instanceof RPsiOpen) {
                RPsiQualifiedPathElement moduleInContext = follow(elementInContext, 0);
                if (moduleInContext != null) {
                    // include »X«; include Y;
                    String pathToTest = moduleInContext.getQualifiedName() + "." + visitedPath;
                    // duplication
                    Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, context.project, context.scope);
                    if (!psiModules.isEmpty()) {
                        RPsiModule resolvedModule = psiModules.iterator().next();
                        String moduleQName = resolvedModule == null ? null : resolvedModule.getQualifiedName();
                        if (moduleQName != null) {
                            found = true;
                            visitedElement.putUserData(RESOLUTION, resolvedModule);
                            walkInfo.modulesInContext.add(visitedElement);

                            // File A: include A1; include A2  -->  A2 === A1.A2
                            data.addValue(getIndex(visitedElement), moduleQName);

                            // File A: include X  -->  A === X
                            if (topLevel && isInclude) {
                                data.addValue("", moduleQName);
                            }

                            break;
                        }
                    }
                }
            }
        }

        // If nothing found, try direct access
        if (!found) {
            RPsiQualifiedPathElement globalResolvedElement = findGlobalModule(walkInfo.path, visitedPath, context);
            if (globalResolvedElement instanceof RPsiModule globalModule) {
                String moduleQName = globalModule.getQualifiedName();
                if (moduleQName != null) {
                    visitedElement.putUserData(RESOLUTION, globalModule);
                    walkInfo.modulesInContext.add(visitedElement);

                    // File A: include X  -->  A === X
                    if (topLevel && isInclude) {
                        data.addValue("", moduleQName);
                    }

                    // File A: open B; include B2  -->  B2 === B.B2
                    if (!moduleQName.equals(visitedPath)) {
                        data.addValue(getIndex(visitedElement), moduleQName);
                    }
                }
            }
        }
    }

    private static @Nullable RPsiQualifiedPathElement follow(@Nullable PsiElement elementInContext, int guard) {
        if (elementInContext == null) {
            return null;
        }

        if (guard > 10) {
            LOG.warn("Follow reached limit for " + elementInContext + ", " + ORFileUtils.getVirtualFile(elementInContext.getContainingFile()));
            return null;
        }

        RPsiQualifiedPathElement resolvedElement = elementInContext.getUserData(RESOLUTION);
        if (resolvedElement != null && resolvedElement != elementInContext) {
            boolean isRecursive = true;
            if (elementInContext instanceof RPsiQualifiedPathElement qualifiedElementInContext) {
                String qualifiedName = qualifiedElementInContext.getQualifiedName();
                if (qualifiedName != null && qualifiedName.equals(resolvedElement.getQualifiedName())) {
                    LOG.warn("Same resolution than element: " + qualifiedName);
                    isRecursive = false;
                    //} else {
                    //    if (LOG.isTraceEnabled()) {
                    //        LOG.trace("  follow: " + elementInContext + (elementInContext instanceof PsiQualifiedNamedElement ? " [" + ((PsiQualifiedNamedElement) elementInContext).getQualifiedName() + ", " + ORFileUtils.getVirtualFile(elementInContext.getContainingFile()) + "]" : "")
                    //                + ", " + resolvedElement + (resolvedElement instanceof PsiQualifiedNamedElement ? " [" + ((PsiQualifiedNamedElement) resolvedElement).getQualifiedName() + ", " + ORFileUtils.getVirtualFile(resolvedElement.getContainingFile()) + "]" : ""));
                }
            }

            if (isRecursive) {
                RPsiQualifiedPathElement alternateResolution = follow(resolvedElement, guard + 1);
                if (alternateResolution != null) {
                    resolvedElement = alternateResolution;
                }
            }
        }

        return resolvedElement;
    }

    private static @NotNull RPsiQualifiedPathElement fullResolution(@NotNull RPsiModule module) {
        RPsiQualifiedPathElement resolved = follow(module, 0);
        return resolved != null ? resolved : module;
    }

    // Follow a path one element by one element, using alternate names for each resolution
    private static @NotNull ResolvedQName resolveQName(@NotNull String qName, @NotNull Context context, @NotNull WalkInfo walkInfo, @NotNull Map<String, Data> visitedFiles) {
        String[] aliasPath = qName.split("\\.");
        int aliasLength = aliasPath.length;
        RPsiQualifiedPathElement resolvedPart = null;

        boolean found = false;

        // First element of path, iterate backward to find a matching local resolution
        for (int i1 = walkInfo.modulesInContext.size() - 1; i1 >= 0; i1--) {
            PsiElement elementInContext = walkInfo.modulesInContext.get(i1);
            if (elementInContext instanceof RPsiInnerModule moduleInContext) {
                String moduleInContextName = moduleInContext.getModuleName() == null ? "" : moduleInContext.getModuleName();

                if (aliasPath[0].equals(moduleInContextName)) {
                    RPsiQualifiedPathElement resolvedModule = moduleInContext.getUserData(RESOLUTION);
                    resolvedPart = resolvedModule == null ? moduleInContext : resolvedModule;
                    break;
                }
            }
            // Try to combine a previous include/open
            //   module A = { module A1 = {} }; module B = A; include B; module C = A1
            else if (elementInContext instanceof RPsiInclude || elementInContext instanceof RPsiOpen) {
                RPsiQualifiedPathElement moduleInContext = follow(elementInContext, 0);
                if (moduleInContext != null) {
                    String pathToTest = moduleInContext.getQualifiedName() + "." + aliasPath[0];
                    Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(pathToTest, context.project, context.scope);
                    if (!psiModules.isEmpty()) {
                        resolvedPart = fullResolution(psiModules.iterator().next());
                    }
                }
            }
        }

        // First element of path not defined in local file, try global access
        if (resolvedPart == null) {
            resolvedPart = findGlobalModule(walkInfo.path, aliasPath[0], context);
        }

        if (resolvedPart != null) {
            found = aliasLength == 1;

            List<String> resolvedAlternateNames = new ArrayList<>();
            resolvedAlternateNames.add(resolvedPart.getQualifiedName());

            // Maybe resolved module include other modules and we need to add them as alternate names
            PsiFile resolvedContainingFile = resolvedPart.getContainingFile();
            if (resolvedContainingFile instanceof FileBase resolvedContainingFileBase) {
                String resolvedFileModuleName = resolvedContainingFileBase.getModuleName();
                if (!walkInfo.moduleName.equals(resolvedFileModuleName)) {
                    // Maybe resolved module include other modules, and we need to add them as alternate names.
                    // We can’t reuse the gist here because of mutual calls and possibility of stack overflow.

                    if (LOG.isTraceEnabled()) {
                        LOG.trace("  Use another GIST (parts) from " + walkInfo.moduleName + " : " + ORFileUtils.getVirtualFile(resolvedContainingFile));
                    }

                    Data anotherData = walkFile(context, new WalkInfo(resolvedContainingFileBase), visitedFiles);

                    Collection<String> values = anotherData.getValues(resolvedPart);
                    resolvedAlternateNames.addAll(values);
                }
            }

            // Resolve each part of alias, for each alternate names
            for (String resolvedAlternateName : resolvedAlternateNames) {
                String rQualifiedName = resolvedAlternateName;
                for (int i = 1; i < aliasLength; i++) {
                    if (i > 1 && resolvedPart != null) {
                        rQualifiedName = resolvedPart.getQualifiedName();
                    }
                    Collection<RPsiModule> psiModules = ModuleFqnIndex.getElements(rQualifiedName + "." + aliasPath[i], context.project, context.scope);
                    resolvedPart = !psiModules.isEmpty() ? psiModules.iterator().next() : null; // zzz mli/ml
                    if (resolvedPart == null) {
                        break;
                    } else {
                        if (resolvedPart instanceof RPsiInnerModule resolvedPartModule) {
                            String resolvedAlias = resolvedPartModule.getAlias();
                            if (resolvedAlias != null) {
                                PsiFile resolvedPartModuleContainingFile = resolvedPartModule.getContainingFile();
                                if (walkInfo.modulesInContext.getFirst() instanceof FileBase currentFile
                                        && resolvedPartModuleContainingFile instanceof FileBase resolvedPartFileBase) {
                                    if (currentFile != resolvedPartModuleContainingFile) {
                                        Data otherData = walkFile(context, new WalkInfo(resolvedPartFileBase), visitedFiles);
                                        Collection<String> alternateNames = otherData.getValues(resolvedPartModule);
                                        if (!alternateNames.isEmpty()) {
                                            resolvedPart = findGlobalModule(walkInfo.path, alternateNames.iterator().next(), context);
                                        }
                                    }
                                }
                            }
                        }
                        found = i == aliasLength - 1;
                    }
                }

                if (found) {
                    break;
                }
            }
        }

        return new ResolvedQName(found, resolvedPart);
    }


    private static @Nullable RPsiQualifiedPathElement findGlobalModule(@NotNull String path, @NotNull String name, @NotNull Context context) {
        Collection<RPsiModule> modules = context.moduleIndexService.getModules(name, context.project, context.scope);
        if (!modules.isEmpty()) {
            if (modules.size() == 1) {
                return modules.iterator().next();
            } else {
                List<ModuleEx> items = modules.stream().map(m -> {
                    PsiFile file = m.getContainingFile();
                    if (file instanceof FileBase fileBase) {
                        return new ModuleEx(m, ORFileUtils.getParentPath(file), fileBase.isInterface());
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull).sorted(new ModuleExComparator(path)).toList();

                if (LOG.isTraceEnabled()) {
                    LOG.trace("  Many global files found for " + name + " from " + path + ", take first", items);
                }

                return items.getFirst().module;
            }
        }
        return null;
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
            if (prevElementType == types.C_MODULE_DECLARATION || prevElementType == types.C_FUNCTOR_DECLARATION || prevElementType == types.C_INCLUDE
                    || prevElementType == types.C_OPEN || prevElementType == types.C_TAG_START) {
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

    record Context(@NotNull ModuleIndexService moduleIndexService, @NotNull Project project,
                   @NotNull GlobalSearchScope scope) {

    }

    record WalkInfo(@NotNull List<PsiElement> modulesInContext, @NotNull Map<PsiElement, Integer> moduleBindings,
                    @NotNull FileBase file, @NotNull String path, @NotNull String moduleName,
                    @NotNull ORLangTypes types) {

        public WalkInfo(FileBase file) {
            this(new ArrayList<>(), new HashMap<>(), file, ORFileUtils.getParentPath(file), file.getModuleName(), ORTypesUtil.getInstance(file.getLanguage()));
            this.modulesInContext.add(file);
        }
    }

    record ResolvedQName(boolean found, RPsiQualifiedPathElement resolvedElement) {
    }

    record ModuleEx(RPsiModule module, String path, boolean isInterface) {

    }

    static class ModuleExComparator implements Comparator<ModuleEx> {
        private final String myPath;

        ModuleExComparator(String path) {
            myPath = path;
        }

        @Override public int compare(ModuleEx m1, ModuleEx m2) {
            // Same folder, then interfaces, then implementations
            if (!m1.path.equals(m2.path)) {
                if (m1.path.equals(myPath)) {
                    return -1;
                }
                if (m2.path.equals(myPath)) {
                    return 1;
                }
                if (m1.path.contains("_build")) {
                    return 1;
                }
                if (m2.path.contains("_build")) {
                    return -1;
                }
            }

            if (m1.isInterface != m2.isInterface) {
                if (m1.isInterface) {
                    return -1;
                }
                return 1;
            }

            return 0;
        }

        @Override public boolean equals(Object obj) {
            return false;
        }
    }
}
