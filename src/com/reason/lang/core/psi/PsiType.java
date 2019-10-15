package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.PsiTypeStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PsiType extends PsiNameIdentifierOwner, PsiQualifiedNamedElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiTypeStub> {

    @Nullable
    PsiTypeConstrName getConstrName();

    @Nullable
    PsiElement getBinding();

    @NotNull
    Collection<PsiVariantDeclaration> getVariants();

    boolean isAbstract();
}
