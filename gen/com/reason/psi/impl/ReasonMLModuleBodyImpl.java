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

public class ReasonMLModuleBodyImpl extends ASTWrapperPsiElement implements ReasonMLModuleBody {

  public ReasonMLModuleBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ReasonMLVisitor visitor) {
    visitor.visitModuleBody(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ReasonMLVisitor) accept((ReasonMLVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ReasonMLIncludeStatement> getIncludeStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLIncludeStatement.class);
  }

  @Override
  @NotNull
  public List<ReasonMLLetBinding> getLetBindingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLLetBinding.class);
  }

  @Override
  @NotNull
  public List<ReasonMLModuleStatement> getModuleStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLModuleStatement.class);
  }

  @Override
  @NotNull
  public List<ReasonMLTypeStatement> getTypeStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ReasonMLTypeStatement.class);
  }

}
