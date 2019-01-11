package com.reason.lang.core.psi;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public interface PsiLanguageConverter {
    @NotNull
    String asText(@NotNull Language language);
}
