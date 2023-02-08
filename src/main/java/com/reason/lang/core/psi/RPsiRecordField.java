package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface RPsiRecordField extends PsiNameIdentifierOwner, RPsiQualifiedPathElement, NavigatablePsiElement, RPsiSignatureElement, StubBasedPsiElement<RsiRecordFieldStub> {
    @Nullable RPsiFieldValue getValue();
}
