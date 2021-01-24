package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.psi.impl.PsiModuleType;
import com.reason.lang.core.stub.PsiModuleStub;
import org.jetbrains.annotations.Nullable;

public interface PsiInnerModule extends PsiModule, StubBasedPsiElement<PsiModuleStub> {
  @Nullable
  PsiModuleType getModuleType();

  @Nullable
  PsiElement getBody();

  boolean isComponent();

  boolean isModuleType();
}
