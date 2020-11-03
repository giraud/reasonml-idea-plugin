package com.reason.lang.core.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiStructuredElement;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiClassField extends CompositePsiElement
    implements NavigatablePsiElement, PsiNameIdentifierOwner, PsiStructuredElement {

  protected PsiClassField(IElementType type) {
    super(type);
  }

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return PsiTreeUtil.findChildOfType(this, PsiLowerSymbol.class);
  }

  @Override
  public String getName() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier == null ? "" : nameIdentifier.getText();
  }

  @Nullable
  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    return null;
  }

  public ItemPresentation getPresentation() {
    return new ItemPresentation() {
      @Nullable
      @Override
      public String getPresentableText() {
        return getName();
      }

      @Nullable
      @Override
      public String getLocationString() {
        return null;
      }

      @NotNull
      @Override
      public Icon getIcon(boolean unused) {
        return ORIcons.VAL;
      }
    };
  }

  @Nullable
  @Override
  public String toString() {
    return "Class.Field " + getName();
  }
}
