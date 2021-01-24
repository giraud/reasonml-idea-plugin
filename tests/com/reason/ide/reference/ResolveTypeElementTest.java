package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiType;

public class ResolveTypeElementTest extends ORBasePlatformTestCase {

  public void test_Rml_basicSameFile() {
    configureCode("A.rei", "type t; type t' = t<caret>;");

    PsiElement e = myFixture.getElementAtCaret();
    assertEquals("A.t", ((PsiType) e.getParent()).getQualifiedName());
  }

  public void test_Ns_SameNameWithPath() {
    configureCode("A.res", "type t;");
    configureCode("B.res", "type t = A.t<caret>;");

    PsiElement e = myFixture.getElementAtCaret();
    assertEquals("A.t", ((PsiType) e.getParent()).getQualifiedName());
  }

  public void test_Rml_SameNameWithPath() {
    configureCode("A.re", "type t;");
    configureCode("B.re", "type t = A.t<caret>;");

    PsiElement e = myFixture.getElementAtCaret();
    assertEquals("A.t", ((PsiType) e.getParent()).getQualifiedName());
  }

  public void test_Ocl_SameNameWithPath() {
    configureCode("A.ml", "type t");
    configureCode("B.ml", "type t = A.t<caret>");

    PsiElement e = myFixture.getElementAtCaret();
    assertEquals("A.t", ((PsiType) e.getParent()).getQualifiedName());
  }
}
