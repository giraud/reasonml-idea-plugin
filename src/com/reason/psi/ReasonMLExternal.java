package com.reason.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface ReasonMLExternal extends PsiElement {

    @NotNull
    ReasonMLValueName getValueName();

    ItemPresentation getPresentation();

}
