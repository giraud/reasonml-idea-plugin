// This is a generated file. Not intended for manual editing.
package com.reason.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.reason.psi.ReasonMLTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.reason.psi.*;

public class ReasonMLLetBindingImpl extends ASTWrapperPsiElement implements ReasonMLLetBinding {

  public ReasonMLLetBindingImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ReasonMLVisitor visitor) {
    visitor.visitLetBinding(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ReasonMLVisitor) accept((ReasonMLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ReasonMLJsx> getJsxList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLJsx.class);
  }

  @Override
  @NotNull
  public List<ReasonMLLabelName> getLabelNameList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLLabelName.class);
  }

  @Override
  @NotNull
  public List<ReasonMLLetBinding> getLetBindingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLLetBinding.class);
  }

  @Override
  @NotNull
  public ReasonMLLetName getLetName() {
    return findNotNullChildByClass(ReasonMLLetName.class);
  }

  @Override
  @NotNull
  public List<ReasonMLParameter> getParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLParameter.class);
  }

  @Override
  @NotNull
  public List<ReasonMLPatternMatching> getPatternMatchingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLPatternMatching.class);
  }

  @Override
  @Nullable
  public ReasonMLTypeExpr getTypeExpr() {
    return findChildByClass(ReasonMLTypeExpr.class);
  }

  @Override
  @NotNull
  public List<ReasonMLUntypedObject> getUntypedObjectList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLUntypedObject.class);
  }

  @Override
  @NotNull
  public List<ReasonMLValueExpr> getValueExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLValueExpr.class);
  }

  @Override
  @NotNull
  public List<ReasonMLValueName> getValueNameList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLValueName.class);
  }

  public boolean isFunction() {
    return ReasonMLPsiImplUtil.isFunction(this);
  }

}
