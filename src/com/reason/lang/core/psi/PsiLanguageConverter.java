package com.reason.lang.core.psi;

import com.reason.lang.*;
import org.jetbrains.annotations.*;

public interface PsiLanguageConverter {
    @NotNull
    String asText(@Nullable ORLanguageProperties language);
}
