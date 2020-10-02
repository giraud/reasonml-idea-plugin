package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiClassConstructor;
import com.reason.lang.core.psi.PsiClassField;
import com.reason.lang.core.psi.PsiClassMethod;
import com.reason.lang.core.psi.PsiClassParameters;
import com.reason.lang.core.psi.PsiObject;
import com.reason.lang.core.type.ORTypes;
import icons.ORIcons;
import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiClassImpl extends PsiToken<ORTypes> implements PsiClass {

  // region Constructors
  public PsiClassImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }
  // endregion

  // region NamedElement
  @Nullable
  public PsiElement getNameIdentifier() {
    return findChildByClass(PsiLowerIdentifier.class);
  }

  @Nullable
  @Override
  public String getName() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier == null ? "" : nameIdentifier.getText();
  }

  @NotNull
  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    return this;
  }
  // endregion

  @NotNull
  @Override
  public String getPath() {
    return ORUtil.getQualifiedPath(this);
  }

  @NotNull
  @Override
  public String getQualifiedName() {
    String name = getName();
    return name == null ? "" : name;
  }

  @Nullable
  @Override
  public PsiElement getClassBody() {
    return PsiTreeUtil.findChildOfType(this, PsiObject.class);
  }

  @NotNull
  @Override
  public Collection<PsiClassField> getFields() {
    return PsiTreeUtil.findChildrenOfType(getClassBody(), PsiClassField.class);
  }

  @NotNull
  @Override
  public Collection<PsiClassMethod> getMethods() {
    return PsiTreeUtil.findChildrenOfType(getClassBody(), PsiClassMethod.class);
  }

  @NotNull
  @Override
  public Collection<PsiClassParameters> getParameters() {
    return PsiTreeUtil.findChildrenOfType(this, PsiClassParameters.class);
  }

  @Nullable
  @Override
  public PsiClassConstructor getConstructor() {
    return findChildByClass(PsiClassConstructor.class);
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
        return ORIcons.CLASS;
      }
    };
  }

  @Nullable
  @Override
  public String toString() {
    return "Class " + getQualifiedName();
  }
}
