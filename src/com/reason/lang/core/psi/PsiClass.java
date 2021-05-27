package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiClass extends PsiQualifiedPathElement, NavigatablePsiElement, PsiStructuredElement {
    @Nullable
    PsiElement getClassBody();

    @NotNull
    Collection<PsiClassField> getFields();

    @NotNull
    Collection<PsiClassMethod> getMethods();

    @NotNull
    Collection<PsiClassParameters> getParameters();

    @Nullable
    PsiClassConstructor getConstructor();
}
