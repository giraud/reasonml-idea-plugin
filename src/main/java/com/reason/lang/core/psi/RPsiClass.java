package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface RPsiClass extends RPsiQualifiedPathElement, NavigatablePsiElement, RPsiStructuredElement, PsiNameIdentifierOwner, StubBasedPsiElement<RsiClassStub> {
    @Nullable
    PsiElement getClassBody();

    @NotNull
    Collection<RPsiClassField> getFields();

    @NotNull
    Collection<RPsiClassMethod> getMethods();

    @NotNull
    Collection<RPsiParameters> getParameters();

    @Nullable
    RPsiClassConstructor getConstructor();

    @Nullable
    RPsiClassInitializer getInitializer();
}
