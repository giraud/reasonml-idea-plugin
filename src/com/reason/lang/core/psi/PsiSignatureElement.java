package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

public interface PsiSignatureElement extends PsiElement {
    @Nullable
    PsiSignature getSignature();
}
