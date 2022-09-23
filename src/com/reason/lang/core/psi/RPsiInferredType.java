package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

public interface RPsiInferredType extends PsiElement {
    void setInferredType(@NotNull RPsiSignature inferredType);

    @Nullable
    RPsiSignature getInferredType();

    boolean hasInferredType();
}
