// This is a generated file. Not intended for manual editing.
package com.reason.lang.core.psi.ocamlyacc.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.reason.lang.core.psi.ocamlyacc.OclYaccRulePattern;
import com.reason.lang.core.psi.ocamlyacc.OclYaccVisitor;
import org.jetbrains.annotations.NotNull;

public class OclYaccRulePatternImpl extends ASTWrapperPsiElement implements OclYaccRulePattern {

  public OclYaccRulePatternImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OclYaccVisitor visitor) {
    visitor.visitRulePattern(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OclYaccVisitor) accept((OclYaccVisitor) visitor);
    else super.accept(visitor);
  }
}
