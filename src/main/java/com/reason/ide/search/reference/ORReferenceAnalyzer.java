package com.reason.ide.search.reference;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import com.reason.lang.rescript.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

import static com.intellij.openapi.application.ApplicationManager.*;
import static java.util.Collections.*;

public class ORReferenceAnalyzer {
    private static final Log LOG = Log.create("ref.analyzer");
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
        Deque<PsiElement> instructions = new LinkedList<>();
        boolean skipDeclaration = false;

        boolean inPath = isInPath(sourceElement, types);

        // use composite instead of leaf if not in a path
        PsiElement item = inPath ? PsiTreeUtil.prevCodeLeaf(sourceElement) : ORUtil.prevSibling(sourceElement);

        item = item == null ? sourceElement.getParent() : item;
        ASTNode itemNode = item != null ? item.getNode() : null;
        IElementType itemType = itemNode != null ? itemNode.getElementType() : null;

        if (itemType == types.LBRACKET && types instanceof ResTypes) {
            // Js access for Rescript
            item = ORUtil.prevSibling(sourceElement.getParent());
        } else if (itemType == types.INCLUDE || itemType == types.OPEN) {
            // if this is the first element of an include/open, we skip the expression
            PsiElement sourceParent = sourceElement.getParent();
            item = ORUtil.prevSibling(sourceParent);
            item = item == null ? sourceParent.getParent() : item;
        }

        while (item != null) {
            itemNode = item.getNode();
            itemType = itemNode != null ? itemNode.getElementType() : null;

            // Try to detect end of the path of the item
            if (inPath) {
                if (itemType == types.A_MODULE_NAME || itemType == types.A_UPPER_TAG_NAME) {
                    PsiElement prevLeaf = PsiTreeUtil.prevLeaf(item);
                    ASTNode prevLeafNode = prevLeaf != null ? prevLeaf.getNode() : null;
                    IElementType prevLeafType = prevLeafNode != null ? prevLeafNode.getElementType() : null;
                    if (prevLeafType != types.DOT && prevLeafType != types.SHARPSHARP) {
                        inPath = false;
                        instructions.push(new FirstInPath(item));
                    } else {
                        instructions.push(item);
                    }
                } else if (itemType == types.LIDENT) {
                    PsiElement prevPrevItem = PsiTreeUtil.prevCodeLeaf(item);
                    if (prevPrevItem != null) {
                        IElementType prevPrevType = prevPrevItem.getNode().getElementType();
                        if (prevPrevType == types.DOT) {
                            instructions.push(new SymbolField(item, true));
                        } else if (prevPrevType == types.SHARPSHARP) {
                            instructions.push(new SymbolField(item, false));
                        } else {
                            instructions.push(item);
                        }
                    }
                } else if (itemType == types.LPAREN) {
                    // can be a local open    A.(b)   (.re)
                    if (item.getParent() instanceof RPsiLocalOpen) {
                        item = PsiTreeUtil.prevCodeLeaf(item);
                        continue;
                    } else {
                        item = item.getParent();
                        inPath = false;
                    }
                } else if (itemType == types.C_ARRAY && types instanceof ResTypes) {
                    // a|>["b"]<|["c"]
                    PsiElement arrayItem = ORUtil.nextSibling(item.getFirstChild());
                    if (arrayItem != null) {
                        instructions.push(new SymbolField(arrayItem, false));
                    }
                } else if (itemType != types.DOT && itemType != types.SHARPSHARP) {
                    inPath = false;
                }
            }

            // Standard expressions (not a start path)
            if (!inPath) {
                if (item instanceof RPsiLocalOpen) {
                    // Restart the path
                    inPath = true;
                }
                //else if (itemType == types.A_MODULE_NAME) {
                // Directly a module like: include X<caret>
                //instructions.push(new FirstInPath(item));
                //}
                else if (item instanceof RPsiInnerModule) {
                    instructions.push(item);
                } else if (item instanceof RPsiFunctor) {
                    instructions.push(item);
                } else if (item instanceof RPsiOpen || item instanceof RPsiInclude) {
                    instructions.push(item);
                } else if (item instanceof RPsiLetBinding) {
                    // We are starting from inside a let, we don’t want to add the let in the instructions
                    skipDeclaration = true;
                } else if (item instanceof RPsiLet) {
                    if (!skipDeclaration) {
                        instructions.push(item);
                    }
                    skipDeclaration = false;
                } else if (item instanceof RPsiTypeBinding) {
                    skipDeclaration = true;
                } else if (item instanceof RPsiType) {
                    if (!skipDeclaration) {
                        instructions.push(item);
                    }
                    skipDeclaration = false;
                } else if (item instanceof RPsiExternal) {
                    instructions.push(item);
                } else if (item instanceof RPsiException) {
                    instructions.push(item);
                } else if (item instanceof FileBase) {
                    instructions.push(item);
                    break;
                } else if (isLower && item instanceof RPsiParameters) {
                    if (item.getParent() instanceof RPsiFunction) {
                        // Inside a function declaration, need to add all parameters
                        for (PsiElement parameterItem : ((RPsiParameters) item).getParametersList()) {
                            if (parameterItem instanceof RPsiParameterDeclaration) {
                                instructions.push(parameterItem);
                            }
                        }
                    }
                } else if (isLower && item instanceof RPsiTagStart) {
                    instructions.push(item);
                }
            }

            // one step backward
            PsiElement prevItem = ORUtil.prevSibling(item);
            item = prevItem == null ? item.getParent() : prevItem;
        }

        return instructions;
    }

    public static boolean isInPath(@NotNull PsiElement sourceElement, @NotNull ORLangTypes types) {
        PsiElement prevLeaf = PsiTreeUtil.prevLeaf(sourceElement);
        IElementType prevLeafType = ORUtil.getNodeType(prevLeaf);
        return prevLeafType == types.DOT || prevLeafType == types.SHARPSHARP || (prevLeafType == types.LBRACKET && types instanceof ResTypes);
    }

    static @NotNull List<RPsiQualifiedPathElement> resolveInstructions(@NotNull Deque<PsiElement> instructions, @Nullable Set<String> openedModules, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        List<RPsiQualifiedPathElement> result = new ArrayList<>();

        List<ResolutionElement> resolutions = new ArrayList<>(); // temporary resolutions

        // First instruction is always current file, if not there is a problem during parsing
        PsiElement containingFile = instructions.removeFirst();
        if (!(containingFile instanceof FileBase)) {
            return result;
        }

        PsiManager psiManager = PsiManager.getInstance(project);

        // Add all globally opened elements (implicit open)
        if (openedModules != null) {
            LOG.trace("Processing globally opened modules");
            for (String openedModuleName : openedModules) {
                List<RPsiModule> modules = getTopModules(openedModuleName, psiManager, scope);
                RPsiModule module = modules.isEmpty() ? null : modules.get(0);
                if (module != null) {
                    ResolutionElement resolutionElement = new ResolutionElement(module, true);
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(" > global module, add [" + resolutionElement + "]");
                    }
                    resolutions.add(resolutionElement);
                }
            }
        }

        LOG.trace("Instructions to process", instructions);

        while (!instructions.isEmpty()) {
            PsiElement instruction = instructions.removeFirst();
            boolean firstInPath = false;
            if (instruction instanceof FirstInPath firstInstruction) {
                instruction = firstInstruction.getOriginalElement();
                firstInPath = true;
            }

            boolean isLastInstruction = instructions.isEmpty();

            if (instruction instanceof SymbolField foundSymbol) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing field", instruction);
                }

                boolean isRecord = foundSymbol.isRecord;
                for (int i = resolutions.size() - 1; i >= 0; i--) {
                    ResolutionElement resolution = resolutions.get(i);
                    PsiElement resolvedElement = resolution.getOriginalElement();
                    String fieldName = foundSymbol.getValue();

                    if (resolvedElement instanceof RPsiLet resolvedLet) {
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
                        Collection<? extends RPsiField> fields = isRecord ? resolvedType.getRecordFields() : resolvedType.getJsObjectFields();
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
                    } else if (resolvedElement instanceof RPsiField resolvedField) {
                        RPsiFieldValue resolvedFieldValue = resolvedField.getValue();
                        PsiElement resolvedValue = resolvedFieldValue == null ? null : resolvedFieldValue.getFirstChild();
                        // field of field
                        Collection<? extends RPsiField> fields = resolvedValue instanceof RPsiJsObject ? ((RPsiJsObject) resolvedValue).getFields() : resolvedValue instanceof RPsiRecord ? ((RPsiRecord) resolvedValue).getFields() : emptyList();
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
                    }
                }
            } else if (instruction instanceof RPsiLowerSymbol foundLower) {
                String foundLowerText = foundLower.getText();
                String foundLowerName = foundLowerText != null && !foundLowerText.isEmpty() ? (foundLowerText.charAt(0) == '`' || foundLowerText.charAt(0) == '#' ? "#" + foundLowerText.substring(1) : foundLowerText) : foundLowerText;
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing lower symbol", foundLowerText);
                }

                // build potential paths by iterating backward the resolutions
                boolean found = false;
                for (int i = resolutions.size() - 1; i >= 0; i--) {
                    if (found || resolutions.isEmpty()) { // maybe
                        break;
                    }

                    ResolutionElement resolution = resolutions.get(i);
                    PsiElement resolvedElement = resolution.getOriginalElement();

                    if (resolvedElement instanceof RPsiLet resolvedLet && resolvedLet.isDeconstruction() && !resolution.isInContext) {
                        // Special case for let deconstruction
                        List<PsiElement> deconstructedElements = resolvedLet.getDeconstructedElements();
                        for (PsiElement deconstructedElement : deconstructedElements) {
                            if (foundLowerText.equals(deconstructedElement.getText())) {
                                if (instructions.isEmpty()) {
                                    resolutions.clear();
                                    result.add(resolvedLet);
                                    found = true;
                                }
                            }
                        }
                    } else if (resolvedElement instanceof RPsiQualifiedPathElement resolvedQPathElement && !resolution.isInContext) {
                        List<RPsiQualifiedPathElement> resolvedSignatures = null;

                        // Special case for a signature item that has same name than its item
                        // ex: let _ = (store: store) => ...
                        if (resolvedElement instanceof RPsiParameterDeclaration resolvedDeclaration) {
                            RPsiSignature resolvedSignature = resolvedDeclaration.getSignature();
                            if (resolvedSignature != null) {
                                RPsiSignature foundSignatureParent = PsiTreeUtil.getParentOfType(foundLower, RPsiSignature.class);
                                if (resolvedSignature == foundSignatureParent) {
                                    // Do not process
                                    continue;
                                } else if (resolvedSignature.getItems().size() == 1) {
                                    // !!! Stack overflow
                                    // TODO: optimise, do not re-evaluate code outside signature
                                    //PsiElement sourceSignatureItem = resolvedSignature.getItems().get(0).getLastChild();
                                    // TODO: optimise, reuse existing resolutions
                                    //Deque<PsiElement> instructions1 = createInstructions(sourceSignatureItem, true, ORUtil.getTypes(resolvedDeclaration.getLanguage()));
                                    //instructions1.addLast(sourceSignatureItem);
                                    //resolvedSignatures = resolveInstructions(instructions1, openedModules, project, scope);
                                }
                            }
                        } else if (resolvedElement instanceof RPsiType resolvedType) {
                            RPsiVariantDeclaration resolvedVariant = resolvedType.getVariants().stream().filter(variant -> variant.getName().equals(foundLowerName)).findFirst().orElse(null);
                            if (resolvedVariant != null && instructions.isEmpty()) {
                                resolutions.clear();
                                result.add(resolvedVariant);
                                found = true;
                                continue;
                            }
                        }

                        if (foundLowerText.equals(resolvedQPathElement.getName())) {
                            found = true;
                            if (instructions.isEmpty()) {
                                // If latest instruction, it is final result
                                resolutions.clear();
                                result.add(resolvedQPathElement);
                            } else {
                                if (resolvedSignatures != null) {
                                    // a declaration with a signature resolved to its type
                                    for (RPsiQualifiedPathElement resolvedSignature : resolvedSignatures) {
                                        resolutions.add(new ResolutionElement(resolvedSignature, true));
                                    }
                                }
                                // can be a record or an object
                                resolutions.add(new ResolutionElement(resolvedQPathElement, true));
                            }
                        }
                    } else if (resolvedElement instanceof RPsiModule resolvedModule && resolution.isInContext) {
                        // Try to resolve instruction from a module
                        String qName = resolvedModule.getQualifiedName();
                        String resolvedQName = qName != null ? qName : "";
                        for (PsiElement alternateResolvedElement : resolvePath(resolvedQName, project, scope, true, 0)) {
                            if (alternateResolvedElement instanceof RPsiModule alternateResolvedModule) {
                                String pathToResolve = alternateResolvedModule.getQualifiedName() + "." + foundLowerName;

                                // Test val
                                Collection<RPsiVal> vals = ValFqnIndex.getElements(pathToResolve, project, scope);
                                if (!vals.isEmpty()) {
                                    if (instructions.isEmpty()) {
                                        resolutions.clear();
                                    }
                                    result.addAll(vals);
                                    break;
                                } else { // Test let
                                    Collection<RPsiLet> lets = LetFqnIndex.getElements(pathToResolve, project, scope);
                                    if (!lets.isEmpty()) {
                                        if (instructions.isEmpty()) {
                                            resolutions.clear();
                                            result.addAll(lets);
                                        } else {
                                            // can be a record or an object
                                            resolutions.addAll(lets.stream().map(l -> new ResolutionElement(l, true)).toList());
                                        }
                                        break;
                                    } else { // Test externals
                                        Collection<RPsiExternal> externals = ExternalFqnIndex.getElements(pathToResolve, project, scope);
                                        if (!externals.isEmpty()) {
                                            if (instructions.isEmpty()) {
                                                resolutions.clear();
                                            }
                                            result.addAll(externals);
                                            break;
                                        } else { // Test types
                                            Collection<RPsiType> types = TypeFqnIndex.getElements(pathToResolve, project, scope);
                                            if (!types.isEmpty()) {
                                                if (instructions.isEmpty()) {
                                                    resolutions.clear();
                                                }
                                                result.addAll(types);
                                                break;
                                            } else {
                                                Collection<RPsiVariantDeclaration> variants = VariantFqnIndex.getElements(pathToResolve, project, scope);
                                                if (!variants.isEmpty()) {
                                                    if (instructions.isEmpty()) {
                                                        resolutions.clear();
                                                    }
                                                    result.addAll(variants);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // X.Y  (module path)
            // ------------------
            else if (instruction instanceof RPsiUpperSymbol foundUpper) {
                String foundUpperText = foundUpper.getText();
                String foundUpperName = foundUpperText != null && !foundUpperText.isEmpty() ? (foundUpperText.charAt(0) == '`' || foundUpperText.charAt(0) == '#' ? "#" + foundUpperText.substring(1) : foundUpperText) : foundUpperText;

                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing Upper symbol", foundUpperText);
                }

                boolean found = false;
                for (int i = resolutions.size() - 1; i >= 0; i--) {
                    if (found || resolutions.isEmpty()) {
                        break;
                    }

                    ResolutionElement resolution = resolutions.get(i);
                    PsiElement resolvedElement = resolution.getOriginalElement();
                    if (resolvedElement instanceof RPsiModule resolvedModule && !resolution.isInContext) {
                        // Local module can be used as the first element of the path, if the name matches
                        // module M = ...  !inContext
                        // M.xxx
                        if (foundUpperText.equals(resolvedModule.getName())) {
                            found = true;

                            if (instructions.isEmpty()) {
                                // If latest instruction, it is final result
                                result.add(resolvedModule);
                            } else {
                                // Starting point of resolutions, we clear the list
                                resolutions.clear();
                                resolutions.add(new ResolutionElement(resolvedModule, true));

                                Collection<String> alternateNames = ORModuleResolutionPsiGist.getData((FileBase) containingFile).getValues(resolvedElement);
                                for (String alternateQName : alternateNames) {
                                    List<ResolutionElement> resolutionElements = resolvePath(alternateQName, project, scope, !isLastInstruction, 0).stream().map(element -> new ResolutionElement(element, true)).toList();
                                    if (LOG.isTraceEnabled()) {
                                        LOG.trace(" > local module found, add alternate names [" + Joiner.join(", ", resolutionElements) + "]");
                                    }

                                    resolutions.addAll(resolutionElements);

                                    // If latest instruction, it is final result
                                    if (instructions.isEmpty()) {
                                        List<RPsiQualifiedPathElement> foundElements = resolutionElements.stream().map(r -> r.getOriginalElement() instanceof RPsiQualifiedPathElement ? ((RPsiQualifiedPathElement) r.getOriginalElement()) : null).filter(Objects::nonNull).toList();
                                        result.addAll(foundElements);
                                    }
                                }
                            }
                        }
                    }
                    //
                    else if (resolvedElement instanceof RPsiModule resolvedModule) {
                        // upper element is part of a path that has already been resolved,
                        // we try to resolve the current upper value as a path

                        String resolvedQName = resolvedModule.getQualifiedName();
                        String pathToResolve = resolvedQName + "." + foundUpperName;

                        List<ResolutionElement> resolutionElements = resolvePath(pathToResolve, project, scope, !isLastInstruction, 0).stream().map(element -> new ResolutionElement(element, true)).toList();
                        if (LOG.isTraceEnabled()) {
                            LOG.trace(" > local module found, add alternate names [" + Joiner.join(", ", resolutionElements) + "]");
                        }

                        if (!resolutionElements.isEmpty()) {
                            found = true;
                            resolutions.clear();

                            // If latest instruction, it is final result
                            if (isLastInstruction) {
                                List<RPsiQualifiedPathElement> foundElements = resolutionElements.stream().map(r -> r.getOriginalElement() instanceof RPsiQualifiedPathElement ? ((RPsiQualifiedPathElement) r.getOriginalElement()) : null).filter(Objects::nonNull).toList();
                                result.addAll(foundElements);
                            } else {
                                resolutions.addAll(resolutionElements);
                            }
                        }
                        // If it’s the last element, we can be more specific
                        else if (instructions.isEmpty()) {
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
                    }
                    //
                    else if (resolvedElement instanceof RPsiException resolvedException && instructions.isEmpty()) {
                        if (foundUpperText.equals(resolvedException.getName())) {
                            resolutions.clear();
                            result.add(resolvedException);
                            break;

                        }
                    }
                    // Maybe we are resolving a locally defined variant
                    // type t = | Variant; ... Variant<caret>
                    else if (resolvedElement instanceof RPsiType resolvedType && instructions.isEmpty()) {
                        for (RPsiVariantDeclaration variant : resolvedType.getVariants()) {
                            if (variant.getName().equals(foundUpperName)) {
                                found = true;
                                resolutions.clear();
                                result.add(variant);
                                break;
                            }
                        }
                    }
                }

                // For the first element of a path, we can try global modules
                if (!found && firstInPath) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(" > No modules found, try top modules");
                    }

                    // Try to resolve top module directly
                    List<RPsiModule> modules = getTopModules(instruction.getText(), psiManager, scope);
                    List<ResolutionElement> list = modules.stream().map(m -> new ResolutionElement(m, true)).toList();
                    resolutions.addAll(list);
                    found = !list.isEmpty();

                    if (found && instructions.isEmpty()) {
                        resolutions.clear();
                        result.addAll(modules);
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
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing open", foundOpen.getPath());
                }

                // Search for alternate paths using gist
                // This is a recursive function (if not end of instruction)
                ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData((FileBase) containingFile);
                Collection<String> alternateNames = data.getValues(foundOpen); // add path to data ?
                if (alternateNames.isEmpty()) {
                    // No alternate names, it is a direct element
                    // We need to analyze the path and resolve each part
                    List<ResolutionElement> resolvedPaths = resolvePath(foundOpen.getPath(), project, scope, !isLastInstruction, 0).stream().map(element -> new ResolutionElement(element, true)).toList();
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(" > no alternate names, add resolutions: [" + Joiner.join(", ", resolvedPaths) + "]");
                    }
                    resolutions.addAll(resolvedPaths);
                } else {
                    // resolve alternate top level modules
                    for (String alternateName : alternateNames) {
                        List<ResolutionElement> resolvedPaths = resolvePath(alternateName, project, scope, !isLastInstruction, 0).stream().map(element -> new ResolutionElement(element, true)).toList();
                        if (LOG.isTraceEnabled()) {
                            LOG.trace(" > alternate name [" + alternateName + "], add resolutions: [" + Joiner.join(", ", resolvedPaths) + "]");
                        }
                        resolutions.addAll(resolvedPaths);
                    }
                }
            }
            // INCLUDE X
            // ---------
            else if (instruction instanceof RPsiInclude foundInclude) {
                LOG.trace("Processing include");

                // Search for alternate paths using gist
                Collection<String> alternateNames = ORModuleResolutionPsiGist.getData((FileBase) containingFile).getValues(foundInclude);
                if (alternateNames.isEmpty()) {
                    // No alternate names, it is a direct element, we need to analyze the path and resolve each part
                    resolutions.addAll(resolvePath(foundInclude.getIncludePath(), project, scope, !isLastInstruction, 0).stream().map(element -> new ResolutionElement(element, true)).toList());
                } else {
                    // Resolve alternate top level modules
                    for (String alternateName : alternateNames) {
                        resolutions.addAll(resolvePath(alternateName, project, scope, !isLastInstruction, 0).stream().map(element -> new ResolutionElement(element, true)).toList());
                    }
                }
            }
            // START TAG
            // ---------
            else if (instruction instanceof RPsiTagStart) {
                LOG.trace("Processing start tag", instruction);

                if (instructions.getLast() instanceof RPsiTagProperty) {
                    RPsiTagStart foundTag = (RPsiTagStart) instruction;

                    // Search for alternate paths using gist
                    Collection<String> alternateNames = ORModuleResolutionPsiGist.getData((FileBase) containingFile).getValues(foundTag);
                    if (alternateNames.isEmpty()) {
                        // No alternate names, it is a direct element, we need to analyze the path and resolve each part
                        String name = foundTag.getName();
                        if (name != null) {
                            resolutions.addAll(resolvePath(name, project, scope, !isLastInstruction, 0).stream().map(element -> new ResolutionElement(element, true)).toList());
                        }
                    } else {
                        // Resolve alternate top level modules
                        for (String alternateName : alternateNames) {
                            resolutions.addAll(resolvePath(alternateName, project, scope, !isLastInstruction, 0).stream().map(element -> new ResolutionElement(element, true)).toList());
                        }
                    }
                }
            } else if (instruction instanceof RPsiTagProperty foundProperty) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing property", instruction);
                }

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
                // find alternate names if upper element
                ResolutionElement res = new ResolutionElement(instruction);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Add instruction [" + res + "]");
                }
                resolutions.add(res);
            }
        }

        return result;
    }

    private static boolean isInterface(PsiElement element) {
        if (element instanceof FileBase && ((FileBase) element).isInterface()) {
            return true;
        }
        return element instanceof RPsiInnerModule && ((RPsiInnerModule) element).isModuleType();
    }

    public static List<PsiElement> resolvePath(@NotNull String path, @NotNull Project project, @NotNull GlobalSearchScope scope, boolean useAlternateNames, int level) {
        List<PsiElement> pathResolutions = new ArrayList<>();

        if (level < MAX_PATH_RESOLUTION_LEVEL) {
            String[] pathTokens = path.split("\\.");

            List<RPsiModule> topModules = getTopModules(pathTokens[0], PsiManager.getInstance(project), scope);
            if (!topModules.isEmpty()) {
                RPsiModule topLevel = topModules.get(0);
                pathResolutions.add(topLevel);

                ModuleIndexService moduleIndexService = getApplication().getService(ModuleIndexService.class);

                if (useAlternateNames) {
                    // Get all alternate resolutions for top level file
                    ORModuleResolutionPsiGist.Data topLevelData = ORModuleResolutionPsiGist.getData((FileBase) topLevel);
                    for (String topLevelAlternateName : topLevelData.getValues(topLevel)) {
                        Collection<RPsiModule> topLevelAlternates = moduleIndexService.getModules(topLevelAlternateName, project, scope);
                        pathResolutions.addAll(topLevelAlternates);
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
                            if (useAlternateNames) {
                                PsiFile containingFile = module.getContainingFile();
                                ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(containingFile);
                                for (String alternateModuleQName : data.getValues(module)) {
                                    if (!pathToResolve.equals(alternateModuleQName)) {
                                        newPathResolutions.addAll(resolvePath(alternateModuleQName, project, scope, true, level + 1));
                                    }
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

    private static @NotNull List<RPsiModule> getTopModules(@NotNull String name, @NotNull PsiManager psiManager, @NotNull GlobalSearchScope scope) {
        return FileModuleIndexService.getInstance().getContainingFiles(name, scope).stream().map(v -> {
            PsiFile psiFile = psiManager.findFile(v);
            return psiFile instanceof RPsiModule ? (RPsiModule) psiFile : null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    static class FirstInPath extends ORFakeResolvedElement {
        public FirstInPath(@NotNull PsiElement element) {
            super(element);
        }
    }

    static class SymbolField extends ORFakeResolvedElement {
        final boolean isRecord;

        public SymbolField(@NotNull PsiElement element, boolean isRecord) {
            super(element);
            this.isRecord = isRecord;
        }

        public @NotNull String getValue() {
            boolean isString = getOriginalElement() instanceof RPsiLiteralString;
            String text = getText();
            if (text == null) {
                return "";
            }
            return isString ? text.substring(1, text.length() - 1) : text;
        }

        @Override
        public String toString() {
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
            return originalElement + (isInContext ? " -> in context" : "");
        }
    }

}
