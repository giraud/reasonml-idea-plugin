package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

// Using a K to avoid confusion with PsiClass from IntelliJ
public interface RPsiClassMethod extends RPsiQualifiedPathElement, NavigatablePsiElement, PsiNameIdentifierOwner, RPsiStructuredElement, StubBasedPsiElement<RsiClassMethodStub> {
    @Nullable RPsiSignature getSignature();
}
