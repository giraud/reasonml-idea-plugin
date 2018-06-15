package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.stub.PsiValStub;
import org.jetbrains.annotations.NotNull;

public interface PsiVal extends PsiQualifiedNamedElement, PsiNamedElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiValStub> {
    @NotNull
    HMSignature getSignature();
}
