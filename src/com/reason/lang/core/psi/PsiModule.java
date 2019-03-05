package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Common interface to file-based modules and inner modules
 */
public interface PsiModule extends PsiQualifiedNamedElement, NavigatablePsiElement, PsiStructuredElement {
    @NotNull
    Collection<PsiNamedElement> getExpressions();

    @Nullable
    String getAlias();
}
