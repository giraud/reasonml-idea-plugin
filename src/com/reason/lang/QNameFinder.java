package com.reason.lang;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiElement;

public interface QNameFinder {

    @NotNull
    Set<String> extractPotentialPaths(@NotNull PsiElement element, boolean resolveLocalModuleAlias);
}
