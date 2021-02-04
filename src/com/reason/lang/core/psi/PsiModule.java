package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Common interface to file-based modules and inner modules
 */
public interface PsiModule
        extends PsiQualifiedElement, NavigatablePsiElement, PsiStructuredElement {

    boolean isInterface();

    boolean isComponent();

    @Nullable
    String getAlias();

    @Nullable
    String getModuleName();

    @Nullable
    PsiFunctorCall getFunctorCall();

    @NotNull
    Collection<PsiNamedElement> getExpressions(@NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter);

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
}
