package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiSignatureItemImpl extends CompositeTypePsiElement<ORTypes> implements PsiSignatureItem {

    protected PsiSignatureItemImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable PsiNamedParam getNamedParam() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiNamedParam.class);
    }

    public @Nullable String getName() {
        PsiNamedParam param = getNamedParam();
        return param == null ? null : param.getName();
    }

    @Override
    public boolean isNamedItem() {
        return getNamedParam() != null;
    }

    @Override
    public boolean isOptional() {
        PsiNamedParam namedParam = getNamedParam();
        return namedParam != null && namedParam.isOptional();
    }

    @NotNull
    @Override
    public String asText(@NotNull Language language) {
        PsiElement firstChild = getFirstChild();
        if (firstChild instanceof PsiLanguageConverter) {
            return ((PsiLanguageConverter) firstChild).asText(language);
        }
        return getText();
    }

    @NotNull
    @Override
    public String toString() {
        return "Signature item";
    }
}
