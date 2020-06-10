package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * Common interface to file-based modules and inner modules
 */
public interface PsiModule extends PsiQualifiedElement, NavigatablePsiElement, PsiStructuredElement {

    boolean isInterface();

    boolean isComponent();

    @Nullable
    String getAlias();

    @Nullable
    String getModuleName();

    @Nullable
    PsiFunctorCall getFunctorCall();

    @NotNull
    Collection<PsiNameIdentifierOwner> getExpressions(@NotNull ExpressionScope eScope);

    @NotNull
    Collection<PsiModule> getModules();

    @Nullable
    PsiModule getModuleExpression(@Nullable String name);

    @Nullable
    PsiType getTypeExpression(@Nullable String name);

    @Nullable
    PsiLet getLetExpression(@Nullable String name);

    @Nullable
    PsiVal getValExpression(@Nullable String name);

    @NotNull
    List<PsiLet> getLetExpressions();
}
