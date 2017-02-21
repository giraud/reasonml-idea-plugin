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

public class ReasonMLExternalDeclarationImpl extends ASTWrapperPsiElement implements ReasonMLExternalDeclaration {

  public ReasonMLExternalDeclarationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ReasonMLVisitor visitor) {
    visitor.visitExternalDeclaration(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ReasonMLVisitor) accept((ReasonMLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ReasonMLBsDirective> getBsDirectiveList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLBsDirective.class);
  }

  @Override
  @NotNull
  public ReasonMLExternalAlias getExternalAlias() {
    return findNotNullChildByClass(ReasonMLExternalAlias.class);
  }

  @Override
  @Nullable
  public ReasonMLValueName getValueName() {
    return findChildByClass(ReasonMLValueName.class);
  }

}
