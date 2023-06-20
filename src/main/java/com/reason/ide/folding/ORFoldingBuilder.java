package com.reason.ide.folding;

import com.intellij.lang.*;
import com.intellij.lang.folding.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.psi.ocamlgrammar.*;
import com.reason.lang.core.psi.ocamllex.*;
import com.reason.lang.core.psi.ocamlyacc.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.ocamlgrammar.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORFoldingBuilder extends FoldingBuilderEx {
    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        ORLangTypes types = ORUtil.getTypes(root.getLanguage());

        PsiTreeUtil.processElements(root, element -> {
            if (element instanceof RPsiLet) {
                foldLet(descriptors, (RPsiLet) element);
            } else if (element instanceof RPsiType) {
                foldType(descriptors, (RPsiType) element);
            } else if (element instanceof RPsiInnerModule) {
                foldModule(descriptors, (RPsiInnerModule) element);
            } else if (element instanceof RPsiFunction) {
                foldFunction(descriptors, (RPsiFunction) element);
            } else if (element instanceof RPsiFunctor) {
                foldFunctor(descriptors, (RPsiFunctor) element);
            } else if (element instanceof RPsiFunctorResult) {
                foldFunctorResult(descriptors, (RPsiFunctorResult) element);
            } else if (element instanceof RPsiTag) {
                foldTag(descriptors, (RPsiTag) element);
            } else if (element instanceof RPsiPatternMatch) {
                foldPatternMatch(descriptors, (RPsiPatternMatch) element);
            } else if (element instanceof RPsiSwitch) {
                foldSwitch(descriptors, (RPsiSwitch) element);
            } else if (element instanceof RPsiTry) {
                foldTry(descriptors, (RPsiTry) element);
            } else if (types.MULTI_COMMENT == element.getNode().getElementType()) {
                FoldingDescriptor fold = fold(element);
                if (fold != null) {
                    descriptors.add(fold);
                }
            }
            // Lex
            else if (element instanceof RPsiLexRule || element instanceof RPsiOCamlInjection) {
                FoldingDescriptor fold = fold(element);
                if (fold != null) {
                    descriptors.add(fold);
                }
            }
            // Yacc
            else if (element instanceof RPsiYaccHeader || element instanceof RPsiYaccRuleBody) {
                FoldingDescriptor fold = fold(element);
                if (fold != null) {
                    descriptors.add(fold);
                }
            }
            // Grammar
            else if (element instanceof RPsiGrammarVernac || element instanceof RPsiGrammarTactic || element instanceof RPsiGrammarArgument || element instanceof RPsiGrammarGrammar) {
                FoldingDescriptor fold = fold(element);
                if (fold != null) {
                    descriptors.add(fold);
                }
            }


            return true;
        });

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private void foldLet(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiLet letExpression) {
        FoldingDescriptor fold = fold(letExpression.getBinding());
        if (fold != null) {
            descriptors.add(fold);
        }
    }

    private void foldType(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiType typeExpression) {
        PsiElement constrName = ORUtil.findImmediateFirstChildOfClass(typeExpression, RPsiLowerSymbol.class);
        if (constrName != null) {
            PsiElement binding = typeExpression.getBinding();
            if (binding != null && binding.getTextLength() > 5) {
                descriptors.add(new FoldingDescriptor(typeExpression, binding.getTextRange()));
            }
        }
    }

    private void foldModule(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiInnerModule module) {
        FoldingDescriptor foldSignature = fold(module.getModuleType());
        if (foldSignature != null) {
            descriptors.add(foldSignature);
        }

        FoldingDescriptor foldBody = fold(module.getBody());
        if (foldBody != null) {
            descriptors.add(foldBody);
        }
    }

    private void foldFunction(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiFunction func) {
        FoldingDescriptor foldBinding = fold(func.getBody());
        if (foldBinding != null) {
            descriptors.add(foldBinding);
        }
    }

    private void foldFunctor(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiFunctor element) {
        FoldingDescriptor foldBinding = fold(element.getBody());
        if (foldBinding != null) {
            descriptors.add(foldBinding);
        }
    }

    private void foldFunctorResult(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiFunctorResult element) {
        FoldingDescriptor foldBinding = fold(element);
        if (foldBinding != null) {
            descriptors.add(foldBinding);
        }
    }

    private void foldTag(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiTag element) {
        RPsiTagStart start = ORUtil.findImmediateFirstChildOfClass(element, RPsiTagStart.class);
        RPsiTagClose close = start == null ? null : ORUtil.findImmediateFirstChildOfClass(element, RPsiTagClose.class);
        // Auto-closed tags are not foldable
        if (close != null) {
            PsiElement lastChild = start.getLastChild();
            TextRange textRange = TextRange.create(lastChild.getTextOffset(), element.getTextRange().getEndOffset() - 1);
            descriptors.add(new FoldingDescriptor((PsiElement) element, textRange));
        }
    }

    private void foldSwitch(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiSwitch element) {
        RPsiBinaryCondition condition = element.getCondition();
        if (condition != null) {
            int startOffset = condition.getTextOffset() + condition.getTextLength() + 1;
            int endOffset = element.getTextRange().getEndOffset();
            if (startOffset < endOffset) {
                TextRange textRange = TextRange.create(startOffset, endOffset);
                descriptors.add(new FoldingDescriptor((PsiElement) element, textRange));
            }
        }
    }

    private void foldTry(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiTry element) {
        FoldingDescriptor fold = fold(element.getBody());
        if (fold != null) {
            descriptors.add(fold);
        }
    }

    private void foldPatternMatch(@NotNull List<FoldingDescriptor> descriptors, @NotNull RPsiPatternMatch element) {
        FoldingDescriptor fold = fold(element.getBody());
        if (fold != null) {
            descriptors.add(fold);
        }
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        IElementType elementType = node.getElementType();
        if (elementType == RmlTypes.INSTANCE.MULTI_COMMENT) {
            return "/*...*/";
        } else if (elementType == OclTypes.INSTANCE.MULTI_COMMENT) {
            return "(*...*)";
        } else if (elementType == OclTypes.INSTANCE.C_MODULE_TYPE) {
            return "sig...";
        } else if (elementType == OclGrammarTypes.INSTANCE.C_VERNAC) {
            return "vernac...";
        } else if (elementType == OclGrammarTypes.INSTANCE.C_TACTIC) {
            return "tactic...";
        } else if (elementType == OclGrammarTypes.INSTANCE.C_ARGUMENT) {
            return "argument...";
        } else if (elementType == OclGrammarTypes.INSTANCE.C_GRAMMAR) {
            return "grammar...";
        } else if (elementType == OclGrammarTypes.INSTANCE.C_INJECTION) {
            return "ocaml...";
        }

        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }

    @Nullable
    private FoldingDescriptor fold(@Nullable PsiElement element) {
        if (element == null) {
            return null;
        }
        TextRange textRange = element.getTextRange();
        return textRange.getLength() > 5 ? new FoldingDescriptor(element, textRange) : null;
    }
}
