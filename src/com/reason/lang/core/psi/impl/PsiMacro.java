package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

public class PsiMacro extends ORCompositeTypePsiElement<ORTypes> {

    protected PsiMacro(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable String getName() {
        PsiElement name = ORUtil.findImmediateFirstChildOfClass(this, PsiMacroName.class);
        return name == null ? null : name.getText();
    }

    public @Nullable PsiMacroBody getContent() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiMacroBody.class);
    }

    public boolean isRoot() {
        PsiElement name = ORUtil.findImmediateFirstChildOfClass(this, PsiMacroName.class);
        if (name == null) {
            return false;
        }

        if (getLanguage() == RmlLanguage.INSTANCE) {
            PsiElement firstChild = getFirstChild();
            ASTNode node = firstChild == null ? null : firstChild.getNode();
            return node != null && node.getElementType() != RmlTypes.INSTANCE.LBRACKET;
        }

        return name.getText().startsWith("%%");
    }
}
