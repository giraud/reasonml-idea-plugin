package com.reason.lang.core.psi;

import com.reason.lang.core.HMSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiSignatureElement {

    @Nullable
    PsiSignature getSignature();

    @NotNull
    HMSignature getHMSignature();

}
