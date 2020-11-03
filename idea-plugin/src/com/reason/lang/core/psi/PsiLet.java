package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiLet extends PsiVar, PsiSignatureElement, PsiInferredType, PsiQualifiedElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<PsiLetStub> {
  @Nullable
  PsiLetBinding getBinding();

  @Nullable
  PsiFunction getFunction();

  boolean isRecord();

  boolean isJsObject();

  @NotNull
  Collection<PsiRecordField> getRecordFields();

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
