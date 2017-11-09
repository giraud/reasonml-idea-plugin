package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.reason.icons.ReasonMLIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLLet extends ASTWrapperPsiElement implements ReasonMLInferredType {
    private String inferredType = "";

    public ReasonMLLet(ASTNode node) {
        super(node);
    }

    @Nullable
    public ReasonMLValueName getLetName() {
        return findChildByClass(ReasonMLValueName.class);
    }

    @Nullable
    public ReasonMLFunBody getFunctionBody() {
        return findChildByClass(ReasonMLFunBody.class);
    }

    @Nullable
    public ReasonMLLetBinding getLetBinding() {
        return findChildByClass(ReasonMLLetBinding.class);
    }

    private boolean isFunction() {
        return findChildByClass(ReasonMLFunBody.class) != null;
    }

    private boolean isRecursive() {
        // Find first element after the LET
        PsiElement firstChild = getFirstChild();
        PsiElement sibling = firstChild.getNextSibling();
        if (sibling != null && sibling instanceof PsiWhiteSpace) {
            sibling = sibling.getNextSibling();
        }

        return sibling != null && "rec".equals(sibling.getText());
    }

    @Override
    public void setInferredType(String inferredType) {
        this.inferredType = inferredType;
    }

    @Override
    public String getInferredType() {
        return inferredType;
    }

    @Override
    public boolean hasInferredType() {
        return inferredType != null && !inferredType.isEmpty();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                ReasonMLValueName letValueName = getLetName();
                if (letValueName == null) {
                    return "_";
                }

                String letName = letValueName.getText();
                if (isFunction()) {
                    return letName + "(..)" + (isRecursive() ? ": rec" : "");
                }

                return letName + (hasInferredType() ? ": " + getInferredType() : "");
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return isFunction() ? ReasonMLIcons.FUNCTION : ReasonMLIcons.LET;
            }
        };
    }
}
