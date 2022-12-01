package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface RPsiFunctor extends PsiNameIdentifierOwner, RPsiModule, StubBasedPsiElement<PsiModuleStub> {
    @NotNull
    List<RPsiParameterDeclaration> getParameters();

    @Nullable
    RPsiFunctorResult getReturnType();

    @NotNull
    List<RPsiTypeConstraint> getConstraints();
}
