package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiQualifiedNamedElement;

/**
 * Common interface to file-based modules and inner modules
 */
public interface PsiModule extends PsiQualifiedNamedElement, NavigatablePsiElement, PsiStructuredElement {

    @Nullable
    String getAlias();

    @NotNull
    Collection<PsiNameIdentifierOwner> getExpressions();

    @Nullable
    PsiModule getModuleExpression(@Nullable String name);

    @Nullable
    PsiLet getLetExpression(@Nullable String name);

    @Nullable
    PsiVal getValExpression(@Nullable String name);

    boolean isInterface();
}
