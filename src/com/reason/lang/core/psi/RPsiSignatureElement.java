package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

public interface RPsiSignatureElement extends PsiElement {
    @Nullable
    RPsiSignature getSignature();
}
