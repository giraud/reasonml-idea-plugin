package com.reason.lang;

import com.intellij.psi.PsiElement;
import java.util.*;
import org.jetbrains.annotations.NotNull;

public interface QNameFinder {

  @NotNull
  Set<String> extractPotentialPaths(@NotNull PsiElement element);
}
