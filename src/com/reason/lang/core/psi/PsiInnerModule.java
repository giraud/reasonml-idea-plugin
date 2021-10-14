package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface PsiInnerModule extends PsiModule, StubBasedPsiElement<PsiModuleStub> {
    boolean isComponent();

    boolean isFunctorCall();

    @Nullable
    PsiModuleType getModuleType();

    @Nullable
    PsiElement getBody();
}
