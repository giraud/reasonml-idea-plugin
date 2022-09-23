package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiSignatureItemImpl extends ORCompositePsiElement<ORTypes> implements RPsiSignatureItem {
    protected RPsiSignatureItemImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiParameterDeclaration getNamedParam() {
        RPsiParameterDeclaration parameter = ORUtil.findImmediateFirstChildOfClass(this, RPsiParameterDeclaration.class);
        return parameter != null && parameter.isNamed() ? parameter : null;
    }

    public @Nullable String getName() {
        RPsiParameterDeclaration param = getNamedParam();
        return param == null ? null : param.getName();
    }

    @Override
    public boolean isNamedItem() {
        return getNamedParam() != null;
    }

    @Override
    public boolean isOptional() {
        RPsiParameterDeclaration namedParam = getNamedParam();
        return namedParam != null && namedParam.isOptional();
    }

    @Override
    public @Nullable PsiElement getSignature() {
        RPsiParameterDeclaration param = getNamedParam();
        return param == null ? null : param.getSignature();
    }

    @Override public PsiElement getDefaultValue() {
        RPsiParameterDeclaration param = getNamedParam();
        return param == null ? null : param.getDefaultValue();
    }

    @Override
    public @NotNull String asText(@Nullable ORLanguageProperties toLang) {
        PsiElement firstChild = getFirstChild();
        if (firstChild instanceof RPsiLanguageConverter) {
            return ((RPsiLanguageConverter) firstChild).asText(toLang);
        }
        return getText();
    }
}
