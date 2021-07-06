package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

public interface PsiAnnotation extends PsiNameIdentifierOwner {
    @Nullable PsiElement getValue();
}
