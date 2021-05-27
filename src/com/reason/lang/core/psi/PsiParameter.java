package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface PsiParameter extends PsiNameIdentifierOwner, PsiQualifiedPathElement, PsiSignatureElement, StubBasedPsiElement<PsiParameterStub> {
    @Nullable
    PsiDefaultValue getDefaultValue();

    boolean isOptional();
}
