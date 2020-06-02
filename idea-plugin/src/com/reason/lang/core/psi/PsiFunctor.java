package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface PsiFunctor extends PsiNameIdentifierOwner, PsiQualifiedElement, NavigatablePsiElement, PsiStructuredElement {
    @Nullable
    PsiFunctorBinding getBinding();

    @NotNull
    Collection<PsiParameter> getParameters();

    @Nullable
    PsiElement getReturnType();
}
