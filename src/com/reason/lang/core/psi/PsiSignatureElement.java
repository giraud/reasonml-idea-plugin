package com.reason.lang.core.psi;

import org.jetbrains.annotations.*;

public interface PsiSignatureElement {
    @Nullable
    PsiSignature getSignature();
}
