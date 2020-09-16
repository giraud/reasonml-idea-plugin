package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;

public interface PsiSwitch extends PsiElement {
    @Nullable PsiBinaryCondition getCondition();

    @NotNull List<PsiPatternMatch> getPatterns();
}
