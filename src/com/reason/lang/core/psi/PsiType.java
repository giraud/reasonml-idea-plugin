package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiType extends PsiNameIdentifierOwner, PsiQualifiedPathElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiTypeStub> {
    @Nullable
    PsiElement getBinding();

    @NotNull
    Collection<PsiVariantDeclaration> getVariants();

    boolean isJsObject();

    boolean isRecord();

    boolean isAbstract();
}
