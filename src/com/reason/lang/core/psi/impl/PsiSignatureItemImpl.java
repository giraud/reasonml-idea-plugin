package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiSignatureItemImpl extends ORCompositePsiElement<ORTypes> implements PsiSignatureItem {
    protected PsiSignatureItemImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable PsiNamedParameterDeclaration getNamedParam() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiNamedParameterDeclaration.class);
    }

    public @Nullable String getName() {
        PsiNamedParameterDeclaration param = getNamedParam();
        return param == null ? null : param.getName();
    }

    @Override
    public boolean isNamedItem() {
        return getNamedParam() != null;
    }

    @Override
    public boolean isOptional() {
        PsiNamedParameterDeclaration namedParam = getNamedParam();
        return namedParam != null && namedParam.isOptional();
    }

    @Override
    public @Nullable PsiElement getSignature() {
        PsiNamedParameterDeclaration param = ORUtil.findImmediateFirstChildOfClass(this, PsiNamedParameterDeclaration.class);
        return param == null ? null : param.getSignature();
    }

    @Override public PsiElement getDefaultValue() {
        PsiNamedParameterDeclaration param = ORUtil.findImmediateFirstChildOfClass(this, PsiNamedParameterDeclaration.class);
        return param == null ? null : param.getDefaultValue();
    }

    @Override
    public @NotNull String asText(@Nullable ORLanguageProperties toLang) {
        PsiElement firstChild = getFirstChild();
        if (firstChild instanceof PsiLanguageConverter) {
            return ((PsiLanguageConverter) firstChild).asText(toLang);
        }
        return getText();
    }
}
