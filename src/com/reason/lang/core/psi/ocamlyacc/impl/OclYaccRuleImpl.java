// This is a generated file. Not intended for manual editing.
package com.reason.lang.core.psi.ocamlyacc.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElementVisitor;
import com.reason.lang.core.psi.ocamlyacc.OclYaccRule;
import com.reason.lang.core.psi.ocamlyacc.OclYaccRuleBody;
import com.reason.lang.core.psi.ocamlyacc.OclYaccVisitor;
import com.reason.lang.ocamlyacc.OclYaccPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OclYaccRuleImpl extends ASTWrapperPsiElement implements OclYaccRule {

  public OclYaccRuleImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OclYaccVisitor visitor) {
    visitor.visitRule(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OclYaccVisitor) accept((OclYaccVisitor) visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public OclYaccRuleBody getRuleBody() {
    return findChildByClass(OclYaccRuleBody.class);
  }

  public ItemPresentation getPresentation() {
    return OclYaccPsiImplUtil.getPresentation(this);
  }
}
