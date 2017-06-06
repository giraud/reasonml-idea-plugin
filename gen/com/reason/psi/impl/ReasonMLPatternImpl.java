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

public class ReasonMLPatternImpl extends ASTWrapperPsiElement implements ReasonMLPattern {

  public ReasonMLPatternImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ReasonMLVisitor visitor) {
    visitor.visitPattern(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ReasonMLVisitor) accept((ReasonMLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ReasonMLConstant getConstant() {
    return findChildByClass(ReasonMLConstant.class);
  }

  @Override
  @Nullable
  public ReasonMLModulePath getModulePath() {
    return findChildByClass(ReasonMLModulePath.class);
  }

  @Override
  @NotNull
  public List<ReasonMLPattern> getPatternList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLPattern.class);
  }

  @Override
  @Nullable
  public ReasonMLTagName getTagName() {
    return findChildByClass(ReasonMLTagName.class);
  }

  @Override
  @NotNull
  public List<ReasonMLValueName> getValueNameList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLValueName.class);
  }

  @Override
  @Nullable
  public ReasonMLValuePath getValuePath() {
    return findChildByClass(ReasonMLValuePath.class);
  }

}
