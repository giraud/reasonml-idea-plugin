package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.psi.impl.PsiConstraint;
import com.reason.lang.core.psi.impl.PsiFunctorBinding;
import com.reason.lang.core.stub.PsiModuleStub;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiFunctor
    extends PsiNameIdentifierOwner, PsiModule, StubBasedPsiElement<PsiModuleStub> {

  @Nullable
  PsiFunctorBinding getBinding();

  @NotNull
  Collection<PsiParameter> getParameters();

  @Nullable
  PsiElement getReturnType();

  @NotNull
  Collection<PsiConstraint> getConstraints();
}
