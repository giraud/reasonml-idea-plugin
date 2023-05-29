package com.reason.lang.core.psi.ocamlyacc;

import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.ocamlyacc.*;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

import javax.swing.*;

public class RPisYaccDeclaration extends ASTWrapperPsiElement implements RPsiStructuredElement {
    public RPisYaccDeclaration(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                PsiElement firstChild = getFirstChild();
                String type = firstChild.getText().substring(1);

                PsiElement sibling = ORUtil.nextSibling(firstChild);
                if (sibling != null && sibling.getNode().getElementType() == OclYaccTypes.INSTANCE.IDENT) {
                    return type + " " + sibling.getText();
                }

                return type;
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.TYPE;
            }
        };
    }
}
