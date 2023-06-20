package com.reason.ide.search.reference;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.intellij.util.indexing.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

import static java.util.Collections.*;

public class ORReferenceAnalyzer {
    private static final int MAX_PATH_RESOLUTION_LEVEL = 10;
    private static final Comparator<PsiElement> SORT_INTERFACE_FIRST = (e1, e2) -> {
        if (isInterface(e1)) {
            return -1;
        }
        if (isInterface(e2)) {
            return 1;
        }
        return 0;
    };

    private ORReferenceAnalyzer() {
    }

    // Walk through the file - from element up to the root - and extract instructions
    static @NotNull Deque<PsiElement> createInstructions(@NotNull PsiElement sourceElement, boolean isLower, @NotNull ORLangTypes types) {
        boolean startPath = true;
        PsiElement prevItem = ORUtil.prevSibling(sourceElement);

        // if caret is already at a UIdent or LIdent
        if ((sourceElement instanceof RPsiUpperSymbol || sourceElement instanceof RPsiLowerSymbol) && prevItem != null) {
            IElementType prevType = prevItem.getNode().getElementType();
            if (prevType == types.RIGHT_ARROW || prevType == types.PIPE_FORWARD || prevType == types.COMMA) {
                // -> A.B   |> A.B
                // we are no more in a path, skip path
                prevItem = prevItem.getPrevSibling();
                ASTNode prevItemNode = prevItem == null ? null : prevItem.getNode();
                prevType = prevItemNode == null ? null : prevItemNode.getElementType();
                while (prevType != null && (prevType == types.DOT || prevType == types.UIDENT || prevType == types.A_VARIANT_NAME || prevType == types.LIDENT)) {
                    prevItem = prevItem.getPrevSibling();
                    prevItemNode = prevItem == null ? null : prevItem.getNode();
                    prevType = prevItemNode == null ? null : prevItemNode.getElementType();
                }

                // if LocalOpen found, it is still a path
                if (prevType == types.LPAREN) {
                    PsiElement parent = prevItem.getParent();
                    startPath = parent instanceof RPsiLocalOpen;
                } else {
                    startPath = false;
                }
            }
        }

        PsiElement item = prevItem == null ? sourceElement.getParent() : prevItem;

        Deque<PsiElement> instructions = new LinkedList<>();
        boolean skipLet = false;
        boolean skipType = false;

        while (item != null) {
            ASTNode itemNode = item.getNode();
            if (itemNode != null && itemNode.getElementType() != types.SEMI) {
                if (startPath && (item instanceof RPsiUpperSymbol || item instanceof RPsiLowerSymbol)) {
                    // only add if it's from a local path
                    //   can be a real path from a record : a.b.c
                    //   or a simulated path from a js object field : a##b##c
                    IElementType nextSiblingNodeType = item.getNextSibling().getNode().getElementType();
                    if ((nextSiblingNodeType == types.DOT || nextSiblingNodeType == types.SHARPSHARP)) {
                        boolean isJsObjectField = item instanceof RPsiLowerSymbol && ORUtil.isPrevType(item, types.SHARPSHARP);
                        if (isJsObjectField) {
                            instructions.push(new LowerSymbolField(item, false));
                        } else {
                            boolean isRecordField = item instanceof RPsiLowerSymbol && ORUtil.isPrevType(item, types.DOT) && ORUtil.prevPrevSibling(item) instanceof RPsiLowerSymbol;
                            instructions.push(isRecordField ? new LowerSymbolField(item, true) : item);
                        }
                    }
                } else if (item instanceof RPsiFunctor) {
                    instructions.push(item);
                } else if (item instanceof RPsiOpen || item instanceof RPsiInclude) {
                    instructions.push(item);
                } else if (item instanceof RPsiLet) {
                    if (!skipLet) {
                        instructions.push(item);
                    }
                    skipLet = false;
                } else if (item instanceof RPsiLetBinding) {
                    skipLet = true;
                } else if (item instanceof RPsiType) {
                    if (!skipType) {
                        instructions.push(item);
                    }
                    skipType = false;
                } else if (item instanceof RPsiTypeBinding) {
                    skipType = true;
                } else if (item instanceof RPsiExternal) {
                    instructions.push(item);
                } else if (item instanceof FileBase) {
                    instructions.push(item);
                    break;
                } else if (item instanceof RPsiModuleBinding) {
                    item = item.getParent();
                } else if (item instanceof RPsiModuleType) {
                    // equivalent to a RPsiModule also
                    instructions.push(item);
                    item = item.getParent();
                } else if (isLower && item instanceof RPsiParameters) {
                    if (item.getParent() instanceof RPsiFunction) {
                        // Inside a function declaration, need to add all parameters
                        for (PsiElement parameterItem : ((RPsiParameters) item).getParametersList()) {
                            if (parameterItem instanceof RPsiParameterDeclaration) {
                                instructions.push(parameterItem);
                            }
                        }
                    }
                }
            }

            // one step backward
            prevItem = ORUtil.prevSibling(item);
            ASTNode prevItemNode = prevItem == null ? null : prevItem.getNode();
            IElementType prevType = prevItemNode == null ? null : prevItemNode.getElementType();

            // Try to detect end of the path of the start item
            if (startPath) {
                if (item instanceof RPsiPatternMatchBody) {
                    startPath = false;
                } else if (prevType == null) {
                    // if LPAREN, we need to analyze context: a localOpen is still part of the path
                    IElementType itemType = itemNode == null ? null : itemNode.getElementType();
                    PsiElement parent = item.getParent();
                    startPath = itemType == types.LPAREN && parent instanceof RPsiLocalOpen;
                } else if (prevType != types.DOT && prevType != types.A_MODULE_NAME && prevType != types.A_UPPER_TAG_NAME && prevType != types.LIDENT && prevType != types.SHARPSHARP/*Rml*/) {
                    // if LocalOpen found, it is still a path
                    if (prevType == types.LPAREN) {
                        PsiElement parent = prevItem.getParent();
                        startPath = parent instanceof RPsiLocalOpen;
                    } else {
                        startPath = false;
                    }
                }
            }

            if (prevItem == null) {
                item = item.getParent();
                // If we are resolving a property inside a
                if (isLower && item instanceof RPsiTagStart) {
                    instructions.push(item);
                }
            } else {
                item = prevItem;
            }
        }

        return instructions;
    }

    static @NotNull List<RPsiQualifiedPathElement> resolveInstructions(@NotNull Deque<PsiElement> instructions, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        List<RPsiQualifiedPathElement> result = new ArrayList<>();

        List<ResolutionElement> resolutions = new ArrayList<>(); // temporary resolutions

        // First instruction is always current file, if not there is a problem during parsing
        PsiElement firstElement = instructions.removeFirst();
        if (!(firstElement instanceof FileBase)) {
            return result;
        }
        resolutions.add(new ResolutionElement(firstElement, true));

        while (!instructions.isEmpty()) {
            PsiElement instruction = instructions.removeFirst();

            if (instruction instanceof LowerSymbolField) {
                boolean isRecord = ((LowerSymbolField) instruction).isRecord;
                for (int i = resolutions.size() - 1; i > 0; i--) { // !! Exclude local file
                    ResolutionElement resolution = resolutions.get(i);
                    PsiElement resolvedElement = resolution.getOriginalElement();
                    if (resolvedElement instanceof RPsiLet resolvedLet) {
                        String fieldName = instruction.getText();
                        Collection<? extends RPsiField> fields = isRecord ? resolvedLet.getRecordFields() : resolvedLet.getJsObjectFields();
                        RPsiField field = fields.stream().filter(f -> fieldName.equals(f.getName())).findFirst().orElse(null);
                        if (field != null) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                                result.add(field);
                            } else {
                                resolutions.add(new ResolutionElement(field, true));
                            }
                        }
                        break;
                    } else if (resolvedElement instanceof RPsiType resolvedType) {
                        String fieldName = instruction.getText();
                        Collection<? extends RPsiField> fields = isRecord ? resolvedType.getRecordFields() : resolvedType.getJsObjectFields();
                        RPsiField field = fields.stream().filter(f -> fieldName.equals(f.getName())).findFirst().orElse(null);
                        if (field != null) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                                result.add(field);
                            } else {
                                resolutions.add(new ResolutionElement(field));
                            }
                        }
                        break;
                    } else if (resolvedElement instanceof RPsiField) {
                        String fieldName = instruction.getText();
                        RPsiFieldValue resolvedFieldValue = ((RPsiField) resolvedElement).getValue();
                        PsiElement resolvedValue = resolvedFieldValue == null ? null : resolvedFieldValue.getFirstChild();
                        // field of field
                        Collection<? extends RPsiField> fields = resolvedValue instanceof RPsiJsObject ? ((RPsiJsObject) resolvedValue).getFields() : resolvedValue instanceof RPsiRecord ? ((RPsiRecord) resolvedValue).getFields() : emptyList();
                        RPsiField field = fields.stream().filter(f -> fieldName.equals(f.getName())).findFirst().orElse(null);
                        if (field != null) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                                result.add(field);
                            } else {
                                resolutions.add(new ResolutionElement(field));
                            }
                        }
                        break;
                    }
                }
            } else if (instruction instanceof RPsiLowerSymbol) {
                // build potential paths by iterating backward the resolutions
                for (int i = resolutions.size() - 1; i > 0; i--) { // !! Exclude local file
                    ResolutionElement resolution = resolutions.get(i);
                    PsiElement resolvedElement = resolution.getOriginalElement();
                    if (resolvedElement instanceof RPsiLet resolvedLet) {
                        if (resolvedLet.isDeconstruction()) {
                            boolean found = false;
                            List<PsiElement> deconstructedElements = resolvedLet.getDeconstructedElements();
                            for (PsiElement deconstructedElement : deconstructedElements) {
                                if (instruction.getText().equals(deconstructedElement.getText())) {
                                    if (instructions.isEmpty()) {
                                        resolutions.clear();
                                        result.add(resolvedLet);
                                        found = true;
                                    }
                                    break;
                                }
                            }
                            if (found) {
                                break;
                            }
                        } else if (instruction.getText().equals(resolvedLet.getName())) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                                result.add(resolvedLet);
                            }
                            break;
                        }
                    } else if (resolvedElement instanceof RPsiParameterDeclaration resolvedParameter) {
                        if (instruction.getText().equals(resolvedParameter.getName())) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                                result.add(resolvedParameter);
                            }
                            break;
                        }
                    } else if (resolvedElement instanceof RPsiType resolvedType) {
                        if (instruction.getText().equals(resolvedType.getName())) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                                result.add(resolvedType);
                            }
                            break;
                        }
                    } else if (resolvedElement instanceof RPsiExternal resolvedExternal) {
                        if (instruction.getText().equals(resolvedExternal.getName())) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                                result.add(resolvedExternal);
                            }
                            break;
                        }
                    } else if (resolution.isInContext && (resolvedElement instanceof RPsiModule)) {
                        // We first try to resolve the instruction using qname index
                        String moduleQName = resolvedElement instanceof FileBase ? ((FileBase) resolvedElement).getModuleName() : ((RPsiModule) resolvedElement).getQualifiedName();
                        String pathToResolve = moduleQName + "." + instruction.getText();

                        // Test val
                        Collection<RPsiVal> vals = ValFqnIndex.getElements(pathToResolve, project, scope);
                        if (!vals.isEmpty()) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                            }
                            result.addAll(vals);
                            break;
                        }

                        // Test let
                        Collection<RPsiLet> lets = LetFqnIndex.getElements(pathToResolve, project, scope);
                        if (!lets.isEmpty()) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                                result.addAll(lets);
                            } else {
                                resolutions.add(new ResolutionElement(lets.iterator().next(), true));
                            }
                            break;
                        }

                        // Test externals
                        Collection<RPsiExternal> externals = ExternalFqnIndex.getElements(pathToResolve, project, scope);
                        if (!externals.isEmpty()) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                            }
                            result.addAll(externals);
                            break;
                        }

                        // Test types
                        Collection<RPsiType> types = TypeFqnIndex.getElements(pathToResolve, project, scope);
                        if (!types.isEmpty()) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                            }
                            result.addAll(types);
                            break;
                        }
                    }
                }
            }
            // X.Y  (module path)
            // ------------------
            else if (instruction instanceof RPsiUpperSymbol) {
                boolean found = false;
                for (int i = resolutions.size() - 1; i >= 0; i--) {
                    ResolutionElement resolution = resolutions.get(i);
                    PsiElement resolvedElement = resolution.getOriginalElement();
                    if (resolvedElement instanceof RPsiModule) {
                        // We first try to resolve the instruction using qname index
                        String moduleQName = resolvedElement instanceof FileBase ? ((FileBase) resolvedElement).getModuleName() : ((RPsiModule) resolvedElement).getQualifiedName();
                        // If name is equal to inner module name, use module qname directly, ex: module X = ...; »X«.y
                        String pathToResolve = moduleQName + "." + instruction.getText();
                        if (resolvedElement instanceof RPsiInnerModule && instruction.getText().equals(((RPsiInnerModule) resolvedElement).getModuleName())) {
                            pathToResolve = ((RPsiInnerModule) resolvedElement).getQualifiedName();
                            if (pathToResolve == null) {
                                break;
                            }
                        }

                        // If it’s the last element, we can be more specific
                        if (instructions.isEmpty()) {
                            // Test if it’s an exception
                            Collection<RPsiException> exceptions = ExceptionFqnIndex.getElements(pathToResolve, project, scope);
                            if (!exceptions.isEmpty()) {
                                resolutions.clear();
                                result.addAll(exceptions);
                                break;
                            }

                            // Else test if it’s a variant
                            Collection<RPsiVariantDeclaration> variants = VariantFqnIndex.getElements(pathToResolve, project, scope);
                            if (!variants.isEmpty()) {
                                resolutions.clear();
                                result.addAll(variants);
                                break;
                            }
                        }

                        Collection<RPsiModule> modules = ModuleFqnIndex.getElements(pathToResolve, project, scope);
                        if (modules.isEmpty()) {
                            // Try to resolve top module directly
                            modules = getTopModules(instruction.getText(), project, scope);
                        }
                        if (!modules.isEmpty()) {
                            if (instructions.isEmpty()) {
                                resolutions.clear();
                            }
                            for (RPsiModule foundModule : modules) {
                                if (foundModule instanceof RPsiFunctor foundFunctor) {
                                    resolutions.clear();
                                    result.add(foundFunctor);
                                    break;
                                } else if (foundModule instanceof RPsiInnerModule foundInnerModule) {

                                    // If it’s the last instruction, we must not resolve the aliases
                                    if (instructions.isEmpty()) {
                                        resolutions.clear();
                                        result.add(foundInnerModule);
                                    } else {
                                        String foundAlias = foundInnerModule.getAlias();
                                        if (foundAlias == null && !(foundInnerModule.isFunctorCall())) {
                                            resolutions.add(new ResolutionElement(foundInnerModule, true));
                                            found = true;
                                        } else {
                                            ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(foundInnerModule.getContainingFile());
                                            Collection<String> alternateNames = data.getElement(foundInnerModule);
                                            if (alternateNames.isEmpty()) {
                                                if (foundInnerModule.isFunctorCall()) {
                                                    // Resolve using qName
                                                    RPsiFunctorCall functorCall = foundInnerModule.getFunctorCall();
                                                    String functorQName = ORUtil.getLongIdent(functorCall == null ? null : functorCall.getFirstChild());
                                                    List<ResolutionElement> elements = resolvePath(functorQName, project, scope, 0).stream().map(element -> new ResolutionElement(element, true)).collect(Collectors.toList());
                                                    resolutions.addAll(elements);
                                                    found = !elements.isEmpty();
                                                }
                                            } else {
                                                // resolve alternate modules
                                                for (String alternateName : alternateNames) {
                                                    // Sort interface first
                                                    List<ResolutionElement> elements = resolvePath(alternateName, project, scope, 0).stream().map(element -> new ResolutionElement(element, true)).collect(Collectors.toList());
                                                    resolutions.addAll(elements);
                                                    found = !elements.isEmpty();
                                                }
                                            }
                                        }
                                    }
                                } else { // top level module
                                    FileBase foundTopLevelModule = (FileBase) foundModule;
                                    if (instructions.isEmpty()) {
                                        result.add(foundTopLevelModule);
                                        continue;
                                    }

                                    resolutions.add(new ResolutionElement(foundTopLevelModule, true));
                                    found = true;

                                    // Search for alternate paths using gist
                                    ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(foundTopLevelModule);
                                    // resolve alternate top level modules
                                    for (String alternateName : data.getElement(foundTopLevelModule)) {
                                        Collection<RPsiModule> alternateElements = ModuleIndexService.getService().getModules(alternateName, project, scope);
                                        resolutions.addAll(alternateElements.stream().map(module -> new ResolutionElement(module, true)).collect(Collectors.toList()));
                                    }
                                }
                                // else
                            }

                            break;
                        }
                    }
                }

                if (!found) {
                    // A path must be resolved/found to continue, else we stop everything
                    instructions.clear();
                    resolutions.clear();
                    break;
                }
            }
            // OPEN X
            // ------
            else if (instruction instanceof RPsiOpen foundOpen) {

                // Search for alternate paths using gist
                // This is a recursive function (if not end of instruction)
                ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData((FileBase) firstElement);
                Collection<String> alternateNames = data.getElement(foundOpen); // add path to data ?
                if (alternateNames.isEmpty()) {
                    // No alternate names, it is a direct element
                    // We need to analyze the path and resolve each part
                    List<ResolutionElement> psiElements = resolvePath(foundOpen.getPath(), project, scope, 0).stream().map(element -> new ResolutionElement(element, true)).collect(Collectors.toList());
                    resolutions.addAll(psiElements);
                } else {
                    // resolve alternate top level modules
                    for (String alternateName : alternateNames) {
                        List<ResolutionElement> psiElements = resolvePath(alternateName, project, scope, 0).stream().map(element -> new ResolutionElement(element, true)).collect(Collectors.toList());
                        resolutions.addAll(psiElements);
                    }
                }
            }
            // INCLUDE X
            // ---------
            else if (instruction instanceof RPsiInclude foundInclude) {

                // Search for alternate paths using gist
                Collection<String> alternateNames = ORModuleResolutionPsiGist.getData((FileBase) firstElement).getElement(foundInclude);
                if (alternateNames.isEmpty()) {
                    // No alternate names, it is a direct element, we need to analyze the path and resolve each part
                    resolutions.addAll(resolvePath(foundInclude.getIncludePath(), project, scope, 0).stream().map(element -> new ResolutionElement(element, true)).collect(Collectors.toList()));
                } else {
                    // Resolve alternate top level modules
                    for (String alternateName : alternateNames) {
                        resolutions.addAll(resolvePath(alternateName, project, scope, 0).stream().map(element -> new ResolutionElement(element, true)).collect(Collectors.toList()));
                    }
                }
            }
            // START TAG
            // ---------
            else if (instruction instanceof RPsiTagStart) {
                if (instructions.getLast() instanceof RPsiTagProperty) {
                    RPsiTagStart foundTag = (RPsiTagStart) instruction;

                    // Search for alternate paths using gist
                    Collection<String> alternateNames = ORModuleResolutionPsiGist.getData((FileBase) firstElement).getElement(foundTag);
                    if (alternateNames.isEmpty()) {
                        // No alternate names, it is a direct element, we need to analyze the path and resolve each part
                        String name = foundTag.getName();
                        if (name != null) {
                            resolutions.addAll(resolvePath(name, project, scope, 0).stream().map(element -> new ResolutionElement(element, true)).collect(Collectors.toList()));
                        }
                    } else {
                        // Resolve alternate top level modules
                        for (String alternateName : alternateNames) {
                            resolutions.addAll(resolvePath(alternateName, project, scope, 0).stream().map(element -> new ResolutionElement(element, true)).collect(Collectors.toList()));
                        }
                    }
                }
            } else if (instruction instanceof RPsiTagProperty foundProperty) {
                // Previous element should be the start tag
                ResolutionElement tag = resolutions.get(resolutions.size() - 1);
                if (tag.isInContext && tag.isComponent()) {
                    String propertyQName = tag.getQualifiedName() + ".make[" + foundProperty.getName() + "]";
                    Collection<RPsiParameterDeclaration> parameters = ParameterFqnIndex.getElements(propertyQName, project, scope);
                    if (!parameters.isEmpty() && instructions.isEmpty()) {
                        resolutions.clear();
                        result.add(parameters.iterator().next());
                    }
                }
            } else {
                resolutions.add(new ResolutionElement(instruction));
            }
        }

        return result;
    }

    private static boolean isInterface(PsiElement element) {
        if (element instanceof FileBase && ((FileBase) element).isInterface()) {
            return true;
        }
        return element instanceof RPsiInnerModule && ((RPsiInnerModule) element).isInterface();
    }

    public static List<PsiElement> resolvePath(@NotNull String path, @NotNull Project project, @NotNull GlobalSearchScope scope, int level) {
        List<PsiElement> pathResolutions = new ArrayList<>();

        if (level < MAX_PATH_RESOLUTION_LEVEL) {
            String[] pathTokens = path.split("\\.");

            List<RPsiModule> topModules = getTopModules(pathTokens[0], project, scope);
            if (!topModules.isEmpty()) {
                RPsiModule topLevel = topModules.get(0);
                pathResolutions.add(topLevel);

                // Get all alternate resolutions for top level file
                ORModuleResolutionPsiGist.Data topLevelData = ORModuleResolutionPsiGist.getData((FileBase) topLevel);
                for (String topLevelAlternateName : topLevelData.getElement(topLevel)) {
                    Collection<RPsiModule> topLevelAlternates = ModuleIndexService.getService().getModules(topLevelAlternateName, project, scope);
                    if (!topLevelAlternates.isEmpty()) {
                        PsiFile topLevelAlternate = topLevelAlternates.iterator().next().getContainingFile();
                        pathResolutions.add(topLevelAlternate);
                    }
                }

                // Append path token to every resolved element to try to resolve new ones
                for (int i = 1; i < pathTokens.length; i++) {
                    String pathToken = pathTokens[i];
                    List<PsiElement> newPathResolutions = new ArrayList<>();

                    for (PsiElement pathResolution : pathResolutions) {
                        String name = (pathResolution instanceof FileBase) ? ((FileBase) pathResolution).getModuleName() : ((RPsiQualifiedPathElement) pathResolution).getQualifiedName();
                        String pathToResolve = name + "." + pathToken;
                        for (RPsiModule module : ModuleFqnIndex.getElements(pathToResolve, project, scope)) {
                            newPathResolutions.add(module);
                            PsiFile containingFile = module.getContainingFile();
                            ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(containingFile);
                            for (String alternateModuleQName : data.getElement(module)) {
                                if (!pathToResolve.equals(alternateModuleQName)) {
                                    // try to find references of coq cSig.mli Sets.exits: Stm.VCS => Stm.VCS.Branch.Vcs_  (fixme)
                                    newPathResolutions.addAll(resolvePath(alternateModuleQName, project, scope, level + 1));
                                }
                            }
                        }
                    }

                    pathResolutions = newPathResolutions;
                }
            }

            pathResolutions.sort(SORT_INTERFACE_FIRST);
        }

        return pathResolutions;
    }

    private static List<RPsiModule> getTopModules(@NotNull String name, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        PsiManager psiManager = PsiManager.getInstance(project);
        FileModuleIndex index = FileModuleIndex.getInstance();
        ID<String, FileModuleData> indexId = index == null ? null : index.getName();
        if (indexId != null) {
            return FileBasedIndex.getInstance().getContainingFiles(indexId, name, scope).stream().map(v -> {
                PsiFile psiFile = psiManager.findFile(v);
                return psiFile instanceof RPsiModule ? (RPsiModule) psiFile : null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    static class LowerSymbolField extends ORFakeResolvedElement {
        final boolean isRecord;

        public LowerSymbolField(@NotNull PsiElement element, boolean isRecord) {
            super(element);
            this.isRecord = isRecord;
        }

        @Override public String toString() {
            return getOriginalElement() + " (" + getOriginalElement().getText() + ") " + (isRecord ? "record" : "jsField");
        }
    }

    static class ResolutionElement extends ORFakeResolvedElement {
        boolean isInContext = false;

        public ResolutionElement(@NotNull PsiElement element) {
            super(element);
        }

        public ResolutionElement(PsiElement element, boolean inContext) {
            super(element);
            this.isInContext = inContext;
        }

        public boolean isComponent() {
            PsiElement originalElement = getOriginalElement();
            if (originalElement instanceof FileBase) {
                return ((FileBase) originalElement).isComponent();
            }
            return originalElement instanceof RPsiModule && ((RPsiModule) originalElement).isComponent();
        }

        public String getQualifiedName() {
            PsiElement originalElement = getOriginalElement();
            return originalElement instanceof RPsiQualifiedPathElement ? ((RPsiQualifiedPathElement) originalElement).getQualifiedName() : "";
        }

        @Override public String toString() {
            PsiElement originalElement = getOriginalElement();
            return originalElement + (isInContext ? "-> in context" : "");
        }
    }

}
