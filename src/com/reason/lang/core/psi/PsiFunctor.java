package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import org.jetbrains.annotations.Nullable;

public interface PsiFunctor extends PsiNamedElement, PsiQualifiedNamedElement, NavigatablePsiElement, PsiStructuredElement {
    @Nullable
    PsiFunctorBinding getBinding();

    @Nullable
    PsiParameters getParameters();
}
