package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiTypeStub;
import org.jetbrains.annotations.Nullable;

public interface PsiType extends PsiNamedElement, NavigatablePsiElement, StubBasedPsiElement<PsiTypeStub> {
    @Nullable
    PsiElement getScopedExpression();
}
