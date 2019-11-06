package com.reason.ide.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.ocamlyacc.OclYaccHeader;
import com.reason.lang.core.psi.ocamlyacc.OclYaccRule;
import com.reason.lang.core.psi.ocamlyacc.OclYaccRuleBody;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.core.type.ORTypesUtil;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.ocamlyacc.OclYaccLazyTypes;
import com.reason.lang.reason.RmlLanguage;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        ORTypes types = root.getLanguage() == RmlLanguage.INSTANCE ? RmlTypes.INSTANCE : OclTypes.INSTANCE;

        PsiTreeUtil.processElements(root, element -> {
            if (element instanceof PsiLet) {
                foldLet(descriptors, (PsiLet) element);
            } else if (element instanceof PsiType) {
                foldType(descriptors, (PsiType) element);
            } else if (element instanceof PsiInnerModule) {
                foldModule(descriptors, (PsiInnerModule) element);
            } else if (element instanceof PsiFunction) {
                foldFunction(descriptors, (PsiFunction) element);
            } else if (element instanceof PsiFunctor) {
                foldFunctor(descriptors, (PsiFunctor) element);
            } else if (element instanceof OclYaccHeader) {
                foldHeader(descriptors, (OclYaccHeader) element);
            } else if (element instanceof OclYaccRule) {
                foldRule(descriptors, (OclYaccRule) element);
            } else {
                if (types.COMMENT == element.getNode().getElementType()) {
                    FoldingDescriptor fold = fold(element);
                    if (fold != null) {
                        descriptors.add(fold);
                    }
                }
            }

            return true;
        });

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private void foldLet(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiLet letExpression) {
        FoldingDescriptor fold = fold(letExpression.getBinding());
        if (fold != null) {
            descriptors.add(fold);
        }
    }

    private void foldType(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiType typeExpression) {
        PsiElement constrName = PsiTreeUtil.findChildOfType(typeExpression, PsiTypeConstrName.class);
        if (constrName != null) {
            ORTypes types = ORTypesUtil.getInstance(typeExpression.getLanguage());
            PsiElement eqElement = ORUtil.nextSiblingWithTokenType(constrName, types.EQ);
            if (eqElement != null) {
                TextRange eqRange = eqElement.getTextRange();
                TextRange typeRange = typeExpression.getTextRange();
                TextRange textRange = TextRange.create(eqRange.getStartOffset(), typeRange.getEndOffset());
                if (textRange.getLength() > 5) {
                    descriptors.add(new FoldingDescriptor(typeExpression, textRange));
                }
            }
        }
    }

    private void foldModule(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiInnerModule module) {
        FoldingDescriptor foldSignature = fold(module.getSignature());
        if (foldSignature != null) {
            descriptors.add(foldSignature);
        }

        FoldingDescriptor foldBody = fold(module.getBody());
        if (foldBody != null) {
            descriptors.add(foldBody);
        }
    }

    private void foldFunction(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiFunction func) {
        FoldingDescriptor foldBinding = fold(func.getBody());
        if (foldBinding != null) {
            descriptors.add(foldBinding);
        }
    }

    private void foldFunctor(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiFunctor functor) {
        FoldingDescriptor foldBinding = fold(functor.getBinding());
        if (foldBinding != null) {
            descriptors.add(foldBinding);
        }
    }

    private void foldHeader(@NotNull List<FoldingDescriptor> descriptors, @NotNull OclYaccHeader root) {
        FoldingDescriptor fold = fold(ORUtil.findImmediateFirstChildOfType(root, OclYaccLazyTypes.OCAML_LAZY_NODE));
        if (fold != null) {
            descriptors.add(fold);
        }
    }

    private void foldRule(@NotNull List<FoldingDescriptor> descriptors, @NotNull OclYaccRule root) {
        FoldingDescriptor fold = fold(ORUtil.findImmediateFirstChildOfClass(root, OclYaccRuleBody.class));
        if (fold != null) {
            descriptors.add(fold);
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        IElementType elementType = node.getElementType();
        if (elementType == RmlTypes.INSTANCE.COMMENT) {
            return "/*...*/";
        } else if (elementType == OclTypes.INSTANCE.COMMENT) {
            return "(*...*)";
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
