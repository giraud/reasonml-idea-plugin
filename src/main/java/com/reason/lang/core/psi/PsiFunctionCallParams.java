package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PsiFunctionCallParams extends PsiElement {
    @NotNull
    Collection<PsiParameter> getParameterList();
}
