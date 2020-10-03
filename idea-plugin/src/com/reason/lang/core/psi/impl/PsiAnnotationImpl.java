package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiMacroName;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiAnnotationImpl extends PsiToken<ORTypes> implements PsiAnnotation {

  public PsiAnnotationImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return findNotNullChildByClass(PsiMacroName.class);
  }

  @Nullable
  @Override
  public String getName() {
    PsiElement identifier = getNameIdentifier();
    return identifier == null ? null : identifier.getText();
  }

  @NotNull
  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    return this;
  }

  @Override
  public @Nullable String toString() {
    return "Annotation " + getName();
  }
}
