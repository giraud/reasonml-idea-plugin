package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiAnnotationImpl extends CompositeTypePsiElement<ORTypes> implements PsiAnnotation {

  protected PsiAnnotationImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return findChildByClass(PsiMacroName.class);
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
