package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.stub.PsiLetStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface PsiLet extends PsiInferredType, PsiQualifiedNamedElement, PsiNamedElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiLetStub> {

    @Nullable
    PsiLetBinding getBinding();

    @NotNull
    HMSignature getSignature();

    @NotNull
    Map<String, String> getParameters();

    boolean isObject();

    boolean isFunction();

    Collection<PsiRecordField> getObjectFields();
}
