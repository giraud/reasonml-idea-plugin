package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiLetStub;
import org.jetbrains.annotations.Nullable;

public interface PsiLet extends PsiInferredType, PsiNamedElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiLetStub> {

    @Nullable
    PsiFunBody getFunctionBody();

    @Nullable
    PsiLetBinding getLetBinding();

}
