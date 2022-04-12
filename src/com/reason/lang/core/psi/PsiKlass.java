package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

// Using a K to avoid confusion with PsiClass from IntelliJ
public interface PsiKlass extends PsiQualifiedPathElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiKlassStub> {
    @Nullable
    PsiElement getClassBody();

    @NotNull
    Collection<PsiClassField> getFields();

    @NotNull
    Collection<PsiClassMethod> getMethods();

    @NotNull
    Collection<PsiParameters> getParameters();

    @Nullable
    PsiClassConstructor getConstructor();
}
