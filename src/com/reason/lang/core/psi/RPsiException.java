package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface RPsiException extends NavigatablePsiElement, RPsiStructuredElement, PsiNameIdentifierOwner, RPsiQualifiedPathElement, StubBasedPsiElement<PsiExceptionStub> {
    @Nullable
    String getAlias();
}
