package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiExternalStub;

public interface PsiExternal
        extends PsiNameIdentifierOwner, PsiQualifiedElement, PsiSignatureElement, PsiStructuredElement, StubBasedPsiElement<PsiExternalStub> {

    boolean isFunction();

    @NotNull
    String getExternalName();
}
