package com.reason.lang.core.psi.ocamlyacc;

import com.intellij.navigation.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

import javax.swing.*;

public class RPsiYaccRule extends ASTWrapperPsiElement implements RPsiStructuredElement {
    public RPsiYaccRule(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                return getFirstChild().getText();
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.FUNCTION;
            }
        };
    }
}
