package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Common interface to file-based modules and inner modules
 */
public interface RPsiModule extends RPsiQualifiedPathElement, NavigatablePsiElement, RPsiStructuredElement {

    boolean isInterface();

    boolean isComponent();

    @Nullable
    String getAlias();

    @Nullable
    RPsiUpperSymbol getAliasSymbol();

    @Nullable
    String getModuleName();

    @Nullable
    String[] getQualifiedNameAsPath();

    @NotNull
    Collection<PsiNamedElement> getExpressions(@NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter);

    @NotNull
    Collection<RPsiModule> getModules();

    @Nullable
    RPsiModule getModuleExpression(@Nullable String name);

    @Nullable
    RPsiLet getLetExpression(@Nullable String name);

    @Nullable
    RPsiModuleType getModuleType();

    @Nullable
    PsiElement getBody();

    @Nullable
    PsiElement getComponentNavigationElement();
}
