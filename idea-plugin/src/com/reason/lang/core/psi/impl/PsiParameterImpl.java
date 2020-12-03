package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiParameterImpl extends PsiTokenStub<ORTypes, PsiParameterStub>
    implements PsiParameter {

  // region Constructors
  public PsiParameterImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  public PsiParameterImpl(
      @NotNull ORTypes types, @NotNull PsiParameterStub stub, @NotNull IStubElementType nodeType) {
    super(types, stub, nodeType);
  }
  // endregion

  @Nullable
  public PsiElement getNameIdentifier() {
    PsiElement parent = getParent();
    PsiElement grandParent = parent == null ? null : parent.getParent();
    if (parent instanceof PsiFunctionCallParams || grandParent instanceof PsiFunctorCall) {
      return null;
    }

    PsiElement identifier = getFirstChild();
    if (identifier != null && identifier.getNode().getElementType() == m_types.TILDE) {
      return identifier.getNextSibling();
    }

    return identifier;
  }

  @Override
  public @Nullable String getName() {
    PsiElement identifier = getNameIdentifier();
    if (identifier != null) {
      return identifier.getText();
    }

    PsiElement parent = getParent();
    PsiElement grandParent = parent == null ? null : parent.getParent();

    if (parent instanceof PsiFunctionCallParams || grandParent instanceof PsiFunctorCall) {
      List<PsiParameter> parameters = parent instanceof PsiFunctionCallParams ?
                                          ((PsiFunctionCallParams) parent).getParametersList() :
                                          ((PsiParameters) parent).getParametersList();
      int i = 0;
      for (PsiParameter parameter : parameters) {
        if (parameter == this) {
          PsiElement prevSibling = /*grandParent instanceof PsiFunctorCall ? null : */ parent.getPrevSibling();
          return (prevSibling == null ? "" : prevSibling.getText()) + "[" + i + "]";
        }
        i++;
      }
    }

    return null;
  }

  @NotNull
  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    return this;
  }

  @Override
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
  public boolean hasDefaultValue() {
    return ORUtil.nextSiblingWithTokenType(getFirstChild(), m_types.EQ) != null;
  }


  @Override
  public @NotNull String getPath() {
    PsiQualifiedElement qualifiedParent = PsiTreeUtil.getParentOfType(this, PsiQualifiedElement.class);
    String parentQName = qualifiedParent == null ? null : qualifiedParent.getQualifiedName();
    return parentQName == null ? "" : parentQName;
  }

  @NotNull
  @Override
  public String getQualifiedName() {
    PsiParameterStub stub = getGreenStub();
    if (stub != null) {
      return stub.getQualifiedName();
    }

    PsiElement parent = getParent();
    PsiElement grandParent = parent == null ? null : parent.getParent();
    boolean isCall = parent instanceof PsiFunctionCallParams || grandParent instanceof PsiFunctorCall;

    String name = getName();
    String path = getPath();
    return path + (isCall ? "." + name : "[" + name + "]");
  }

  @Override
  public @NotNull String toString() {
    return "Parameter " + getQualifiedName();
  }
}
