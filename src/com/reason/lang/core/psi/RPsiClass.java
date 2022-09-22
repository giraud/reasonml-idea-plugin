package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

// Using a K to avoid confusion with PsiClass from IntelliJ
public interface RPsiClass extends PsiQualifiedPathElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<RsiClassStub> {
    @Nullable
    PsiElement getClassBody();

    @NotNull
    Collection<RPsiClassField> getFields();

    @NotNull
    Collection<RPsiClassMethod> getMethods();

    @NotNull
    Collection<PsiParameters> getParameters();

    @Nullable
    RPsiClassConstructor getConstructor();
}
