// This is a generated file. Not intended for manual editing.
package com.reason.lang.core.psi.ocamlyacc.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.reason.lang.core.psi.ocamlyacc.*;
import com.reason.lang.ocamlyacc.OclYaccPsiImplUtil;
import com.intellij.navigation.ItemPresentation;

public class OclYaccTrailerImpl extends ASTWrapperPsiElement implements OclYaccTrailer {

  public OclYaccTrailerImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OclYaccVisitor visitor) {
    visitor.visitTrailer(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OclYaccVisitor) accept((OclYaccVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ItemPresentation getPresentation() {
    return OclYaccPsiImplUtil.getPresentation(this);
  }

}
