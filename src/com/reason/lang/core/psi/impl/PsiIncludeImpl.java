package com.reason.lang.core.psi.impl;

import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.xml.model.gotosymbol.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

public class PsiIncludeImpl extends CompositeTypePsiElement<ORTypes> implements PsiInclude {
    // region Constructors
    protected PsiIncludeImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
    // endregion

    @Override
    public @NotNull String getPath() {
        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        if (firstChild instanceof PsiFunctorCall) {
            return ((PsiFunctorCall) firstChild).getFunctorName();
        }
        return firstChild == null ? "" : ORUtil.getTextUntilClass(firstChild, PsiConstraints.class);
    }

    @Override
    public boolean useFunctor() {
        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        return firstChild instanceof PsiFunctorCall;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new GoToSymbolProvider.BaseNavigationItem(this, getPath(), ORIcons.INCLUDE);
    }

    @Override
    public boolean canBeDisplayed() {
        return !(getParent() instanceof PsiFunctionBody);
    }

    @Override
    public @NotNull String toString() {
        return "PsiInclude " + getPath();
    }
}
