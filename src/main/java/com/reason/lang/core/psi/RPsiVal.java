package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;

public interface RPsiVal extends RPsiVar, RPsiQualifiedPathElement, RPsiSignatureElement, NavigatablePsiElement, RPsiStructuredElement, StubBasedPsiElement<PsiValStub> {
}
