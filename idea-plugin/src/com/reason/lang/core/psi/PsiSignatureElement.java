package com.reason.lang.core.psi;

import com.reason.lang.core.signature.ORSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiSignatureElement {

  @Nullable
  PsiSignature getPsiSignature();

  @NotNull
  ORSignature getORSignature();
}
