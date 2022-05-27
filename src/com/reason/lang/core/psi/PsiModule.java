package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Common interface to file-based modules and inner modules
 */
public interface PsiModule extends PsiQualifiedPathElement, NavigatablePsiElement, PsiStructuredElement {

    boolean isInterface();

    boolean isComponent();

    @Nullable
    String getAlias();

    @Nullable
    PsiUpperSymbol getAliasSymbol();

    @Nullable
    String getModuleName();

    @Nullable
    String[] getQualifiedNameAsPath();

    @NotNull
    Collection<PsiNamedElement> getExpressions(@NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter);

    @NotNull
    Collection<PsiModule> getModules();

    @Nullable
    PsiModule getModuleExpression(@Nullable String name);

    @Nullable
    PsiLet getLetExpression(@Nullable String name);

    @Nullable
    PsiElement getModuleType();

    @Nullable
    PsiElement getBody();

    @Nullable
    PsiElement getComponentNavigationElement();
}
