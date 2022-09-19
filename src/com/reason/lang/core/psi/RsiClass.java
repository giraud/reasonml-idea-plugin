package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

// Using a K to avoid confusion with PsiClass from IntelliJ
public interface RsiClass extends PsiQualifiedPathElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<RsiClassStub> {
    @Nullable
    PsiElement getClassBody();

    @NotNull
    Collection<RsiClassField> getFields();

    @NotNull
    Collection<RsiClassMethod> getMethods();

    @NotNull
    Collection<PsiParameters> getParameters();

    @Nullable
    RsiClassConstructor getConstructor();
}
