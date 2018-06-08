package com.reason.lang.core.psi;

import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.stub.PsiExternalStub;
import org.jetbrains.annotations.NotNull;

public interface PsiExternal extends PsiNamedElement, PsiQualifiedNamedElement, PsiStructuredElement, StubBasedPsiElement<PsiExternalStub> {

    @NotNull
    HMSignature getSignature();

    boolean isFunction();

}
