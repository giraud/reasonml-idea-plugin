package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface RPsiLet extends RPsiVar, RPsiSignatureElement, RPsiInferredType, RPsiQualifiedPathElement, NavigatablePsiElement, RPsiStructuredElement, PsiNameIdentifierOwner, StubBasedPsiElement<PsiLetStub> {
    @Nullable
    RPsiLetBinding getBinding();

    @Nullable
    RPsiFunction getFunction();

    boolean isComponent();

    boolean isRecord();

    boolean isJsObject();

    boolean isScopeIdentifier();

    @Nullable
    String getAlias();

    @NotNull
    Collection<PsiElement> getScopeChildren();

    boolean isDeconstruction();

    @NotNull
    List<PsiElement> getDeconstructedElements();

    boolean isPrivate();

    @Nullable
    RPsiFirstClass getFirstClassModule();
}
