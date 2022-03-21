package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiSwitch extends PsiElement {
    @Nullable
    PsiBinaryCondition getCondition();

    @NotNull
    List<PsiPatternMatch> getPatterns();
}
