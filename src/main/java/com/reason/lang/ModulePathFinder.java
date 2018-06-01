package com.reason.lang;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiElement;

public interface ModulePathFinder {
    @NotNull
    List<String> extractPotentialPaths(@NotNull PsiElement element);
}
