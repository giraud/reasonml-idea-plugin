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

public class ReasonMLSTagImpl extends ASTWrapperPsiElement implements ReasonMLSTag {

  public ReasonMLSTagImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ReasonMLVisitor visitor) {
    visitor.visitSTag(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ReasonMLVisitor) accept((ReasonMLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ReasonMLTagName getTagName() {
    return findNotNullChildByClass(ReasonMLTagName.class);
  }

  @Override
  @NotNull
  public List<ReasonMLTagProperty> getTagPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLTagProperty.class);
  }

}
