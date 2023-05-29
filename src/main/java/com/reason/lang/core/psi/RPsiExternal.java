package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface RPsiExternal extends RPsiQualifiedPathElement, RPsiSignatureElement, RPsiStructuredElement, PsiNameIdentifierOwner, StubBasedPsiElement<PsiExternalStub> {
    boolean isFunction();

    @NotNull
    String getExternalName();
}
