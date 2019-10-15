package com.reason.lang.core.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiParameterStub;
import org.jetbrains.annotations.Nullable;

public interface PsiParameter extends PsiNameIdentifierOwner, PsiQualifiedNamedElement, PsiSignatureElement, StubBasedPsiElement<PsiParameterStub> {
    boolean hasDefaultValue();

    @Nullable
    String getName();
}
