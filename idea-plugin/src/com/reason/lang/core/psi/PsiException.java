package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiExceptionStub;
import org.jetbrains.annotations.Nullable;

public interface PsiException
    extends NavigatablePsiElement,
        PsiStructuredElement,
        PsiQualifiedElement,
        StubBasedPsiElement<PsiExceptionStub> {
  @Nullable
  String getAlias();
}
