package com.reason.lang;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface QNameFinder {
    @NotNull Set<String> extractPotentialPaths(@Nullable PsiElement element);
}
