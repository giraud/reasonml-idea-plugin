package com.reason.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.reason.icons.ReasonMLIcons;
import com.reason.psi.impl.ReasonMLInferredTypeMixin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLLet extends ReasonMLInferredTypeMixin {

    public ReasonMLLet(ASTNode node) {
        super(node);
    }

    @NotNull
    public ReasonMLValueName getLetName() {
        return findNotNullChildByClass(ReasonMLValueName.class);
    }

    @Nullable
    public ReasonMLFunBody getFunctionBody() {
        return findChildByClass(ReasonMLFunBody.class);
    }

    public boolean isFunction() {
        return findChildByClass(ReasonMLFunBody.class) != null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                String letName = getLetName().getText();
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
