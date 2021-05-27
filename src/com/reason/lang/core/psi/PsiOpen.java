package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;

public interface PsiOpen extends PsiStructuredElement, StubBasedPsiElement<PsiOpenStub> {
    String getPath();

    boolean useFunctor();
}
