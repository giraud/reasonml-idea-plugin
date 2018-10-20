package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PsiClass extends PsiNamedElement, PsiQualifiedNamedElement, NavigatablePsiElement, PsiStructuredElement {
    @Nullable
    PsiElement getClassBody();

    @NotNull
    Collection<PsiClassField> getFields();

    @NotNull
    Collection<PsiClassMethod> getMethods();

    @NotNull
    Collection<PsiClassParameters> getParameters();

    @Nullable
    PsiClassConstructor getConstructor();
}
