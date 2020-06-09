package com.reason.lang.core.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiParameterStub;

public interface PsiParameter extends PsiNameIdentifierOwner, PsiQualifiedElement, PsiSignatureElement, StubBasedPsiElement<PsiParameterStub> {
    boolean hasDefaultValue();
}
