package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiLetStub;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PsiLet extends PsiHMSignature, PsiInferredType, PsiQualifiedNamedElement, PsiNamedElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiLetStub> {

    @Nullable
    PsiLetBinding getBinding();

    @Nullable
    PsiFunction getFunction();

    boolean isObject();

    boolean isFunction();

    Collection<PsiRecordField> getObjectFields();

    @Nullable
    PsiSignature getSignature();
}
