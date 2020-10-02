package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiClassMethod extends ASTWrapperPsiElement
    implements NavigatablePsiElement, PsiNameIdentifierOwner, PsiStructuredElement {
  public PsiClassMethod(@NotNull ASTNode node) {
    super(node);
  }

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiLowerIdentifier.class);
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

  @Nullable
  public PsiSignature getSignature() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiSignature.class);
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
        PsiSignature signature = getSignature();
        return signature == null ? null : signature.getText();
      }

      @NotNull
      @Override
      public Icon getIcon(boolean unused) {
        return ORIcons.METHOD;
      }
    };
  }

  @Nullable
  @Override
  public String toString() {
    return "Class.Method " + getName();
  }
}
