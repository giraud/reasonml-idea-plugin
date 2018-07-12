package com.reason.lang.core.psi;

import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiExternalStub;

public interface PsiExternal extends PsiNamedElement, PsiQualifiedNamedElement, PsiHMSignature, PsiStructuredElement, StubBasedPsiElement<PsiExternalStub> {

    boolean isFunction();

}
