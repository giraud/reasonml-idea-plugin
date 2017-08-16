package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
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

    private boolean isFunction() {
        return findChildByClass(ReasonMLFunBody.class) != null;
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
                    return letName + "(..)";
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
