package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface RPsiOpen extends RPsiStructuredElement, StubBasedPsiElement<PsiOpenStub> {
    @NotNull String getPath();

    boolean useFunctor();
}
