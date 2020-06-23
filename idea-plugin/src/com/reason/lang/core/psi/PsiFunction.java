package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PsiFunction extends PsiElement {
    @NotNull
    Collection<PsiParameter> getParameters();

    @Nullable
    PsiFunctionBody getBody();
}
