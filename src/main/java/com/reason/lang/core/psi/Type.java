package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.TypeStub;

public interface Type extends NamedElement, StubBasedPsiElement<TypeStub> {
    PsiElement getScopedExpression();
}
