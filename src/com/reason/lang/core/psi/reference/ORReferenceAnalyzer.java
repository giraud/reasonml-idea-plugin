package com.reason.lang.core.psi.reference;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORReferenceAnalyzer {
    private static final String[] EMPTY_ARRAY = new String[0];

    private ORReferenceAnalyzer() {
    }

    static class ORUpperSymbolWithResolution extends ORFakeResolvedElement {
        public ORUpperSymbolWithResolution(@NotNull PsiElement element) {
            super(element);
        }

        @Override public String toString() {
            return getOriginalElement().toString();
        }
    }

    static class ORUpperResolvedSymbol extends ORFakeResolvedElement {
        public ORUpperResolvedSymbol(@NotNull PsiElement element) {
            super(element);
        }

        @Override public String toString() {
            return getOriginalElement().toString();
        }
    }

    static class ORLocalAlias extends ORFakeResolvedElement {
        public final String myResolvedAlias;

        public ORLocalAlias(@NotNull PsiElement element, @NotNull String resolvedAlias) {
            super(element);
            myResolvedAlias = resolvedAlias;
        }

        public boolean isModuleName(@NotNull String name) {
            return name.equals(((PsiModule) getOriginalElement()).getModuleName());
        }

        @Override
        public @NotNull String toString() {
            return ((PsiModule) getOriginalElement()).getModuleName() + " =~ " + myResolvedAlias;
        }
    }

    // Walk through the file - from element up to the root - and extract instructions
    static @NotNull Deque<PsiElement> createInstructions(@NotNull PsiElement sourceElement, @NotNull ORTypes types) {
        boolean startPath = true;
        PsiElement prevItem = ORUtil.prevSibling(sourceElement);
        if ((sourceElement instanceof PsiUpperSymbol || sourceElement instanceof PsiLowerSymbol) && prevItem != null) {
            IElementType prevType = prevItem.getNode().getElementType();
            if (prevType == types.RIGHT_ARROW || prevType == types.PIPE_FORWARD || prevType == types.COMMA) {
                // -> A.B   |> A.B
                // we are no more in a path, skip path
                prevItem = prevItem.getPrevSibling();
                ASTNode prevItemNode = prevItem == null ? null : prevItem.getNode();
                prevType = prevItemNode == null ? null : prevItemNode.getElementType();
                while (prevType != null && (prevType == types.DOT || prevType == types.C_UPPER_SYMBOL || prevType == types.C_VARIANT || prevType == types.C_LOWER_SYMBOL)) {
                    prevItem = prevItem.getPrevSibling();
                    prevItemNode = prevItem == null ? null : prevItem.getNode();
                    prevType = prevItemNode == null ? null : prevItemNode.getElementType();
                }

                // if LocalOpen found, it is still a path
                if (prevType == types.LPAREN) {
                    PsiElement parent = prevItem.getParent();
                    startPath = parent instanceof PsiLocalOpen;
                } else {
                    startPath = false;
                }
            }
        }
        PsiElement item = prevItem == null ? sourceElement.getParent() : prevItem;
        Deque<PsiElement> instructions = new LinkedList<>();

        while (item != null) {
            if (item instanceof PsiUpperSymbol || item instanceof PsiLowerSymbol) {
                // only add if it's from a local path
                //   can be a real path from a record : a.b.c
                //   or a simulated path from a js object field : a##b##c
                IElementType nextSiblingNodeType = item.getNextSibling().getNode().getElementType();
                if ((nextSiblingNodeType == types.DOT || nextSiblingNodeType == types.SHARPSHARP) && startPath) {
                    instructions.push(item instanceof PsiUpperSymbol ? new ORUpperSymbolWithResolution(item) : item);
                }
            } else if (item instanceof PsiInnerModule) {
                if (((PsiInnerModule) item).isFunctorCall()) {
                    PsiFunctorCall functorCall = ORUtil.findImmediateFirstChildOfClass(item, PsiFunctorCall.class);
                    if (functorCall != null) {
                        instructions.push(functorCall);
                        //instructions.push(new ORFakeModuleAlias(item, functorCall.getFunctorName()));
                    }
                } else {
                    instructions.push(item);
                }
            } else if (item instanceof PsiOpen) {
                instructions.push(item);
            } else if (item instanceof PsiLet) {
                instructions.push(item);
            } else if (item instanceof PsiType) {
                instructions.push(item);
            } else if (item instanceof PsiTagStart) {
                instructions.push(item);
            } else if (item instanceof FileBase) {
                instructions.push(item);
                break;
            }

            prevItem = ORUtil.prevSibling(item);
            if ((item instanceof PsiUpperSymbol || item instanceof PsiLowerSymbol) && prevItem != null) {
                IElementType prevType = prevItem.getNode().getElementType();
                if (prevType == types.RIGHT_ARROW || prevType == types.PIPE_FORWARD || prevType == types.COMMA) {
                    // -> A.B   or   |> A.B
                    // we are no more in a path, skip path
                    prevItem = prevItem.getPrevSibling();
                    prevType = prevItem == null ? null : prevItem.getNode().getElementType();
                    while (prevType != null && (prevType == types.DOT || prevType == types.C_UPPER_SYMBOL || prevType == types.C_VARIANT || prevType == types.C_LOWER_SYMBOL)) {
                        prevItem = prevItem.getPrevSibling();
                        prevType = prevItem == null ? null : prevItem.getNode().getElementType();
                    }

                    // if LocalOpen found, it is still a path
                    if (prevType == types.LPAREN) {
                        PsiElement parent = prevItem.getParent();
                        startPath = parent instanceof PsiLocalOpen;
                    } else {
                        startPath = false;
                    }
                }
            } else if (prevItem == null && startPath) {
                // if LPAREN, we need to analyze context: a localOpen is still part of the path
                IElementType itemType = item.getNode().getElementType();
                PsiElement parent = item.getParent();
                startPath = itemType == types.LPAREN && parent instanceof PsiLocalOpen;
            }

            item = prevItem == null ? item.getParent() : prevItem;
        }

        return instructions;
    }

    static @NotNull Deque<CodeInstruction> resolveInstructions(@NotNull Deque<PsiElement> instructions, @NotNull Project project) {
        Deque<CodeInstruction> resolvedInstructions = new LinkedList<>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        while (!instructions.isEmpty()) {
            PsiElement psiElement = instructions.removeFirst();

            if (psiElement instanceof PsiUpperSymbol || psiElement instanceof ORUpperSymbolWithResolution) {
                boolean withResolution = psiElement instanceof ORUpperSymbolWithResolution;
                PsiElement element = withResolution ? psiElement.getOriginalElement() : psiElement;
                String name = element.getText();

                // Try to resolve the local aliases and deconstruct result path
                String[] path = withResolution ?
                        resolvedInstructions.stream()
                                .map(codeInstruction -> codeInstruction.mySource instanceof ORLocalAlias ? (ORLocalAlias) codeInstruction.mySource : null)
                                .filter(item -> item != null && item.isModuleName(name))
                                .findFirst()
                                .map(item -> item.myResolvedAlias.split("\\."))
                                .orElse(null) : null;

                if (path != null) {
                    for (String p : path) {
                        resolvedInstructions.push(new CodeInstruction(element, p));
                    }
                }

                // Try to resolve aliases with full path
                boolean alreadyReplaced = false;
                if (withResolution) {
                    String qname = name;
                    Iterator<CodeInstruction> rIt = resolvedInstructions.iterator();
                    boolean hasNext = rIt.hasNext();
                    while (hasNext) {
                        CodeInstruction instruction = rIt.next();
                        if (instruction.mySource instanceof PsiUpperSymbol) {
                            qname = instruction.mySource.getText() + "." + qname;
                            Collection<PsiModule> elements = ModuleAliasesIndex.getElements(qname, project, scope);
                            if (elements.isEmpty()) {
                                hasNext = rIt.hasNext();
                            } else {
                                // Remove all elements until current instruction
                                CodeInstruction peek = resolvedInstructions.pop();
                                while (peek != null && peek.mySource != instruction.mySource) {
                                    peek = resolvedInstructions.pop();
                                }
                                // and replace them with new path
                                String elementAlias = elements.iterator().next().getAlias();
                                String[] aliasPath = elementAlias == null ? EMPTY_ARRAY : elementAlias.split("\\.");
                                for (String p : aliasPath) {
                                    resolvedInstructions.push(new CodeInstruction(new ORUpperResolvedSymbol(element), p));
                                }
                                hasNext = false;
                                alreadyReplaced = true;
                            }
                        } else {
                            hasNext = false;
                        }
                    }
                }

                if (path == null && !alreadyReplaced) {
                    resolvedInstructions.push(new CodeInstruction(element, name));
                }
            } else if (psiElement instanceof PsiLowerSymbol) {
                resolvedInstructions.push(new CodeInstruction(psiElement, psiElement.getText()));
            } else if (psiElement instanceof PsiInnerModule) {
                String alias = ((PsiInnerModule) psiElement).getAlias();
                if (alias == null) {
                    resolvedInstructions.push(new CodeInstruction(psiElement, ((PsiInnerModule) psiElement).getModuleName()));
                } else {
                    String[] aliasPath = alias.split("\\.");
                    CodeInstruction localAlias = resolvedInstructions.stream().filter(instruction -> instruction.mySource instanceof ORLocalAlias && ((ORLocalAlias) instruction.mySource).isModuleName(aliasPath[0])).findFirst().orElse(null);
                    if (localAlias == null) {
                        PsiElement local = new ORLocalAlias(psiElement, alias);
                        resolvedInstructions.push(new CodeInstruction(local, null));
                    } else {
                        int pos = alias.indexOf(".");
                        if (0 <= pos) {
                            String newAlias = ((ORLocalAlias) localAlias.mySource).myResolvedAlias + alias.substring(pos);
                            PsiElement local = new ORLocalAlias(psiElement, newAlias);
                            resolvedInstructions.push(new CodeInstruction(local, null));
                        }
                    }
                }
            } else if (psiElement instanceof PsiOpen) {
                String[] tokens = ((PsiOpen) psiElement).getPath().split("\\.");
                for (String token : tokens) {
                    resolvedInstructions.push(new CodeInstruction(psiElement, token));
                }
            } else if (psiElement instanceof FileBase) {
                resolvedInstructions.push(new CodeInstruction(psiElement, ((FileBase) psiElement).getModuleName()));
            } else if (psiElement instanceof PsiNamedElement) {
                resolvedInstructions.push(new CodeInstruction(psiElement, ((PsiNamedElement) psiElement).getName()));
            }
        }

        return resolvedInstructions;
    }
}
