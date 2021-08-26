// This is a generated file. Not intended for manual editing.
package com.reason.lang.core.psi.ocamlyacc;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiStructuredElement;

public class OclYaccVisitor extends PsiElementVisitor {

  public void visitDeclaration(@NotNull OclYaccDeclaration o) {
    visitPsiStructuredElement(o);
  }

  public void visitHeader(@NotNull OclYaccHeader o) {
    visitPsiStructuredElement(o);
  }

  public void visitRule(@NotNull OclYaccRule o) {
    visitPsiStructuredElement(o);
  }

  public void visitRuleBody(@NotNull OclYaccRuleBody o) {
    visitPsiElement(o);
  }

  public void visitRulePattern(@NotNull OclYaccRulePattern o) {
    visitPsiElement(o);
  }

  public void visitTrailer(@NotNull OclYaccTrailer o) {
    visitPsiStructuredElement(o);
  }

  public void visitPsiStructuredElement(@NotNull PsiStructuredElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
