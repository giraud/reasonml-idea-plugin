package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiTypeStub;
import org.jetbrains.annotations.Nullable;

public interface PsiType extends PsiNamedElement, PsiQualifiedNamedElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiTypeStub> {
    @Nullable
    PsiElement getScopedExpression();

    String getTypeInfo();
}
