package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface RPsiInnerModule extends RPsiModule, StubBasedPsiElement<PsiModuleStub>, NavigatablePsiElement, PsiNameIdentifierOwner {
    @Nullable
    String getAlias();

    @Nullable
    RPsiUpperSymbol getAliasSymbol();

    boolean isComponent();

    boolean isFunctorCall();

    @Nullable
    RPsiFunctorCall getFunctorCall();

    @Nullable
    RPsiModuleType getModuleType();

    @Nullable
    PsiElement getBody();

    @NotNull
    List<RPsiTypeConstraint> getConstraints();
}
