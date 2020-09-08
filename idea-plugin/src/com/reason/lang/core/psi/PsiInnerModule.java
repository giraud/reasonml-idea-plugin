package com.reason.lang.core.psi;

import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiModuleStub;

public interface PsiInnerModule extends PsiModule, StubBasedPsiElement<PsiModuleStub> {
    @Nullable PsiSignature getSignature();

    @Nullable PsiElement getBody();

    boolean isComponent();

    boolean isModuleType();
}
