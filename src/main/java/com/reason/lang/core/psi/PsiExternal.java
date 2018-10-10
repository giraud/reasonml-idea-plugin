package com.reason.lang.core.psi;

import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiExternalStub;
import org.jetbrains.annotations.NotNull;

public interface PsiExternal extends PsiNamedElement, PsiQualifiedNamedElement, PsiSignatureElement, PsiStructuredElement, StubBasedPsiElement<PsiExternalStub> {

    boolean isFunction();

    @NotNull
    String getExternalName();
}
