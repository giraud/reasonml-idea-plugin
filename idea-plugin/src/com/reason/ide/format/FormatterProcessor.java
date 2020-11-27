package com.reason.ide.format;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

public interface FormatterProcessor {
  @Nullable PsiElement apply(@NotNull String textToFormat);
}
