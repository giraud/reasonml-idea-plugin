package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface PsiInclude extends PsiStructuredElement, StubBasedPsiElement<PsiIncludeStub> {
    String[] getQualifiedPath();

    String @Nullable [] getResolvedPath();

    @NotNull String getIncludePath();

    @Nullable PsiUpperSymbol getModuleReference();
    @Nullable PsiElement resolveModule();

    boolean useFunctor();
}
