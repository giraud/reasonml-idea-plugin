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
import com.intellij.navigation.ItemPresentation;

public class ReasonMLExternalStatementImpl extends ASTWrapperPsiElement implements ReasonMLExternalStatement {

  public ReasonMLExternalStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ReasonMLVisitor visitor) {
    visitor.visitExternalStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ReasonMLVisitor) accept((ReasonMLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ReasonMLExternalAlias getExternalAlias() {
    return findNotNullChildByClass(ReasonMLExternalAlias.class);
  }

  @Override
  @NotNull
  public List<ReasonMLItemAttribute> getItemAttributeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLItemAttribute.class);
  }

  @Override
  @NotNull
  public ReasonMLTypeExpr getTypeExpr() {
    return findNotNullChildByClass(ReasonMLTypeExpr.class);
  }

  @Override
  @NotNull
  public ReasonMLValueName getValueName() {
    return findNotNullChildByClass(ReasonMLValueName.class);
  }

  public ItemPresentation getPresentation() {
    return ReasonMLPsiImplUtil.getPresentation(this);
  }

}
