package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;

public interface RPsiRecordField extends RPsiField, PsiNameIdentifierOwner, RPsiQualifiedPathElement, NavigatablePsiElement, RPsiSignatureElement, StubBasedPsiElement<RsiRecordFieldStub> {
}
