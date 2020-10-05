package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.stub.PsiRecordFieldStub;
import com.reason.lang.core.type.ORTypes;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiRecordFieldImpl extends PsiTokenStub<ORTypes, PsiRecordFieldStub>
    implements PsiRecordField {

  // region Constructors
  public PsiRecordFieldImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  public PsiRecordFieldImpl(
      @NotNull ORTypes types,
      @NotNull PsiRecordFieldStub stub,
      @NotNull IStubElementType nodeType) {
    super(types, stub, nodeType);
  }
  // endregion

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return getFirstChild();
  }

  @NotNull
  @Override
  public String getPath() {
    PsiRecordFieldStub stub = getGreenStub();
    if (stub != null) {
      return stub.getPath();
    }

    PsiType parent = PsiTreeUtil.getParentOfType(this, PsiType.class);
    String name = getName();
    return (parent == null) ? name : (ORUtil.getQualifiedPath(parent) + "." + name);
  }

  @NotNull
  @Override
  public String getQualifiedName() {
    PsiRecordFieldStub stub = getGreenStub();
    if (stub != null) {
      return stub.getQualifiedName();
    }

    return ORUtil.getQualifiedName(this);
  }

  @NotNull
  @Override
  public String getName() {
    PsiElement nameElement = getNameIdentifier();
    return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
  }

  @Nullable
  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    return null;
  }

  @Nullable
  public PsiSignature getPsiSignature() {
    return PsiTreeUtil.findChildOfType(this, PsiSignature.class);
  }

  @NotNull
  @Override
  public ORSignature getORSignature() {
    PsiSignature signature = getPsiSignature();
    return signature == null ? ORSignature.EMPTY : signature.asHMSignature();
  }

  @Override
  public ItemPresentation getPresentation() {
    return new ItemPresentation() {
      @Override
      public @NotNull String getPresentableText() {
        return getName();
      }

      @Override
      public @Nullable String getLocationString() {
        PsiSignature signature = getPsiSignature();
        return signature == null ? null : signature.getText();
      }

      @Override
      public @NotNull Icon getIcon(boolean unused) {
        return ORIcons.VAL;
      }
    };
  }

  @Nullable
  @Override
  public String toString() {
    return "Record field " + getQualifiedName();
  }
}
