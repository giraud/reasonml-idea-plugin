package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiInnerModule extends PsiModule, StubBasedPsiElement<PsiModuleStub> {
    boolean isComponent();

    boolean isFunctorCall();

    @Nullable
    PsiFunctorCall getFunctorCall();

    @Nullable
    PsiModuleType getModuleType();

    @Nullable
    PsiElement getBody();

    @NotNull
    List<PsiTypeConstraint> getConstraints();
}
