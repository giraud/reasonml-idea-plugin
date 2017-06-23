package com.reason.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface ReasonMLType extends PsiElement {

    @NotNull
    ReasonMLTypeConstrName getTypeConstrName();

    ItemPresentation getPresentation();

}
