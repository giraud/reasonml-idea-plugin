package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiRecordFieldStub;

public interface PsiRecordField
        extends PsiNameIdentifierOwner, PsiQualifiedElement, NavigatablePsiElement, PsiSignatureElement, StubBasedPsiElement<PsiRecordFieldStub> {

}
