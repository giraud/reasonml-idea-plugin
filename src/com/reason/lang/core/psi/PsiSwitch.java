package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PsiSwitch extends PsiElement {
    @Nullable
    PsiBinaryCondition getCondition();

    @NotNull
    Collection<?> getPatterns();
}
