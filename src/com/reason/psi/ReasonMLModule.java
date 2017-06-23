package com.reason.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReasonMLModule extends PsiElement {

    @Nullable
    ReasonMLScopedExpr getModuleBody();

    @NotNull
    ReasonMLModuleName getModuleName();

    ItemPresentation getPresentation();

}
