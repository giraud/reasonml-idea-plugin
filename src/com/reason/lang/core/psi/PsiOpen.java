package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface PsiOpen extends PsiStructuredElement, StubBasedPsiElement<PsiOpenStub> {
    @NotNull String getPath();

    boolean useFunctor();
}
