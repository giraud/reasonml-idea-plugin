package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface PsiClass extends PsiQualifiedElement, NavigatablePsiElement, PsiStructuredElement {
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
