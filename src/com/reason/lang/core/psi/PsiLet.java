package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiLetStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface PsiLet extends PsiSignatureElement, PsiInferredType, PsiQualifiedNamedElement, PsiNamedElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiLetStub> {

    @Nullable
    PsiLetBinding getBinding();

    @Nullable
    PsiFunction getFunction();

    boolean isObject();

    boolean isJsObject();

    boolean isFunction();

    @NotNull
    Collection<PsiRecordField> getObjectFields();

    @NotNull
    Collection<PsiJsObjectField> getJsObjectFieldsForPath(List<String> path);

    @Nullable
    PsiSignature getPsiSignature();

    boolean isScopeIdentifier();

    @NotNull
    Collection<PsiElement> getScopeChildren();
}
