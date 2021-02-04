package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

public interface PsiInferredType extends PsiElement {
    void setInferredType(@NotNull PsiSignature inferredType);

    @Nullable
    PsiSignature getInferredType();

    boolean hasInferredType();
}
