package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiFunctor extends PsiNameIdentifierOwner, PsiModule, StubBasedPsiElement<PsiModuleStub> {
    @NotNull
    Collection<PsiParameter> getParameters();

    @Nullable
    PsiFunctorResult getReturnType();

    @NotNull
    Collection<PsiTypeConstraint> getConstraints();
}
