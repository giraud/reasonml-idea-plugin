package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.PsiLetStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface PsiLet extends PsiVar, PsiSignatureElement, PsiInferredType, PsiQualifiedNamedElement, PsiNameIdentifierOwner, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiLetStub> {

    @Nullable
    PsiLetBinding getBinding();

    @Nullable
    PsiFunction getFunction();

    boolean isRecord();

    boolean isJsObject();

    @NotNull
    Collection<PsiRecordField> getRecordFields();

    @NotNull
    Collection<PsiObjectField> getJsObjectFieldsForPath(@NotNull List<String> path);

    @Nullable
    PsiSignature getPsiSignature();

    boolean isScopeIdentifier();

    @Nullable
    String getAlias();

    @NotNull
    Collection<PsiElement> getScopeChildren();

    @NotNull
    String getQualifiedPath();
}
