package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface PsiException extends NavigatablePsiElement, PsiStructuredElement, PsiNameIdentifierOwner, PsiQualifiedPathElement, StubBasedPsiElement<PsiExceptionStub> {
    @Nullable
    String getAlias();
}
