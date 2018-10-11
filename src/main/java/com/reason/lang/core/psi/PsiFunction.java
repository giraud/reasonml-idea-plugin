package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PsiFunction extends PsiElement {
    @Nullable
    PsiFunctionBody getBody();

    @NotNull
    Collection<PsiParameter> getParameterList();
}
