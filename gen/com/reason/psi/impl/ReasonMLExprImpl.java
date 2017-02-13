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

public class ReasonMLExprImpl extends ASTWrapperPsiElement implements ReasonMLExpr {

  public ReasonMLExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ReasonMLVisitor visitor) {
    visitor.visitExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ReasonMLVisitor) accept((ReasonMLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ReasonMLExpr getExpr() {
    return findChildByClass(ReasonMLExpr.class);
  }

  @Override
  @Nullable
  public ReasonMLFunDecl getFunDecl() {
    return findChildByClass(ReasonMLFunDecl.class);
  }

  @Override
  @Nullable
  public ReasonMLJsx getJsx() {
    return findChildByClass(ReasonMLJsx.class);
  }

  @Override
  @Nullable
  public ReasonMLLetBinding getLetBinding() {
    return findChildByClass(ReasonMLLetBinding.class);
  }

  @Override
  @Nullable
  public ReasonMLValueExpr getValueExpr() {
    return findChildByClass(ReasonMLValueExpr.class);
  }

  @Override
  @Nullable
  public ReasonMLValueName getValueName() {
    return findChildByClass(ReasonMLValueName.class);
  }

}
