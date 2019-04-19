package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiExceptionStub;

public interface PsiException extends NavigatablePsiElement, PsiStructuredElement, PsiNamedElement, PsiQualifiedNamedElement, StubBasedPsiElement<PsiExceptionStub> {
}
