package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface RPsiInclude extends RPsiStructuredElement, StubBasedPsiElement<PsiIncludeStub> {
    String[] getQualifiedPath();

    @NotNull String getIncludePath();

    boolean useFunctor();
}
