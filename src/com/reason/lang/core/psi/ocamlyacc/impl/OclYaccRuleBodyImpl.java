// This is a generated file. Not intended for manual editing.
package com.reason.lang.core.psi.ocamlyacc.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.reason.lang.ocamlyacc.OclYaccTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.reason.lang.core.psi.ocamlyacc.*;
import com.reason.lang.ocamlyacc.OclYaccPsiImplUtil;

public class OclYaccRuleBodyImpl extends ASTWrapperPsiElement implements OclYaccRuleBody {

  public OclYaccRuleBodyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OclYaccVisitor visitor) {
    visitor.visitRuleBody(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OclYaccVisitor) accept((OclYaccVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<OclYaccRulePattern> getRulePatternList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, OclYaccRulePattern.class);
  }

}
