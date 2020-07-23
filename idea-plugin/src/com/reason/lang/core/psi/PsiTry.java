package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PsiTry extends PsiToken<ORTypes> {

    public PsiTry(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Nullable
    public PsiElement getBody() {
        return ORUtil.findImmediateFirstChildOfType(this, (IElementType) m_types.C_TRY_BODY);
    }

    @Nullable
    public Collection<PsiElement> getHandlers() {
        PsiElement scopedElement = ORUtil.findImmediateFirstChildOfType(this, (IElementType) m_types.C_TRY_HANDLERS);
        return ORUtil.findImmediateChildrenOfType(scopedElement, (IElementType) m_types.C_TRY_HANDLER);
    }

    @NotNull
    @Override
    public String toString() {
        return "Try";
    }
}
