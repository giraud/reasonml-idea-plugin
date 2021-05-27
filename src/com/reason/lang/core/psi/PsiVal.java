package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;

public interface PsiVal extends PsiVar, PsiQualifiedPathElement, PsiSignatureElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiValStub> {
}
