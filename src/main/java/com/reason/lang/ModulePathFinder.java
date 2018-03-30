package com.reason.lang;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ModulePathFinder {
    List<String> extractPotentialPaths(@NotNull PsiElement element);
}
