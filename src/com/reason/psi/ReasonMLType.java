package com.reason.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReasonMLType extends PsiElement {

    @NotNull
    ReasonMLTypeConstrName getTypeConstrName();

    @Nullable
    ReasonMLScopedExpr getScopedExpression();

    ItemPresentation getPresentation();

}
