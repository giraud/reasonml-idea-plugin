package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiLetStub;

public interface PsiLet
        extends PsiVar, PsiSignatureElement, PsiInferredType, PsiQualifiedElement, PsiNameIdentifierOwner, NavigatablePsiElement, PsiStructuredElement,
                StubBasedPsiElement<PsiLetStub> {

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
    String getPath();

    boolean isDeconsruction();

    @NotNull
    List<PsiElement> getDeconstructedElements();

    boolean isPrivate();
}
