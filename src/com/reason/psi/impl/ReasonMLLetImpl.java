package com.reason.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.reason.icons.ReasonMLIcons;
import com.reason.psi.ReasonMLFunBody;
import com.reason.psi.ReasonMLLet;
import com.reason.psi.ReasonMLValueName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLLetImpl extends ReasonMLInferredTypeMixin implements ReasonMLLet {

    public ReasonMLLetImpl(ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public ReasonMLValueName getLetName() {
        return findNotNullChildByClass(ReasonMLValueName.class);
    }

    @Override
    @Nullable
    public ReasonMLFunBody getFunctionBody() {
        return findChildByClass(ReasonMLFunBody.class);
    }

    @Override
    public boolean isFunction() {
        return findChildByClass(ReasonMLFunBody.class) != null;
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                String letName = getLetName().getText();
                if (isFunction()) {
                    return letName + "(..)";
                }

                return letName /*+ (let.hasInferredType() ? ": " + let.getInferredType() : "")*/;
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
