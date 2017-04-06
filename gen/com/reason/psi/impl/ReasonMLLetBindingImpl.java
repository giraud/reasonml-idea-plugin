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
  public List<ReasonMLExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLExpr.class);
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
  @Nullable
  public ReasonMLTypeExpr getTypeExpr() {
    return findChildByClass(ReasonMLTypeExpr.class);
  }

  @Override
  @Nullable
  public ReasonMLValueName getValueName() {
    return findChildByClass(ReasonMLValueName.class);
  }

  public boolean isFunction() {
    return ReasonMLPsiImplUtil.isFunction(this);
  }

}
