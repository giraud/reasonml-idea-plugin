package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

public interface RPsiSignatureItem extends PsiNamedElement, RPsiLanguageConverter {
    boolean isNamedItem();

    @Nullable String getName();

    boolean isOptional();

    @Nullable PsiElement getSignature();

    @Nullable PsiElement getDefaultValue();
}
