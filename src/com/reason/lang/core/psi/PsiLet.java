package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiLet extends PsiVar, PsiSignatureElement, PsiInferredType, PsiQualifiedPathElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiLetStub> {
    @Nullable
    PsiLetBinding getBinding();

    @Nullable
    PsiFunction getFunction();

    boolean isRecord();

    boolean isJsObject();

    boolean isScopeIdentifier();

    @Nullable
    String getAlias();

    @Nullable
    PsiElement resolveAlias();

    @NotNull
    Collection<PsiElement> getScopeChildren();

    boolean isDeconstruction();

    @NotNull
    List<PsiElement> getDeconstructedElements();

    boolean isPrivate();
}
