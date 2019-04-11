package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PsiFunctor extends PsiNamedElement, PsiQualifiedNamedElement, NavigatablePsiElement, PsiStructuredElement {
    @Nullable
    PsiFunctorBinding getBinding();

    @NotNull
    Collection<PsiParameter> getParameters();

    @Nullable
    PsiElement getReturnType();
}
