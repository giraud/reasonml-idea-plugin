package com.reason.lang;

import com.intellij.psi.PsiElement;
import java.util.*;

import org.jetbrains.annotations.*;

public interface QNameFinder {

  @NotNull
  Set<String> extractPotentialPaths(@Nullable PsiElement element);
}
