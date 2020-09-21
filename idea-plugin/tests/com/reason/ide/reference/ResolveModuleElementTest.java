package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiQualifiedElement;

public class ResolveModuleElementTest extends ORBasePlatformTestCase {

  public void testBasicNull() {
    configureCode("Dimensions.re", "let space = 5;");
    configureCode("Comp.re", "Dimensions<caret>");

    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    assertEquals("Dimensions.re", ((PsiQualifiedElement) elementAtCaret).getName());
  }

  public void testBasic() {
    configureCode("Dimensions.re", "let space = 5;");
    configureCode("Comp.re", "let D = Dimensions<caret>");

    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    assertEquals("Dimensions.re", ((PsiQualifiedElement) elementAtCaret).getName());
  }

  public void testWithAlias() {
    configureCode("A1.re", "module A11 = {};");
    configureCode("A.re", "module A1 = {};");
    configureCode("B.re", "module X = A; X.A1<caret>);");

    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    assertEquals("A.A1", ((PsiQualifiedElement) elementAtCaret.getParent()).getQualifiedName());
  }

  public void test_Rml_withLocalAlias() {
    configureCode("Belt.re", "let x = 1;");
    configureCode("A.re", "module B = Belt; B<caret>");

    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    assertEquals("A.B", ((PsiQualifiedElement) elementAtCaret.getParent()).getQualifiedName());
  }

  public void testWithAliasAndInterface() {
    configureCode("C.rei", "module A1 = {};");
    configureCode("D.re", "module X = C; X.A1<caret>;");

    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    assertEquals("C.A1", ((PsiQualifiedElement) elementAtCaret.getParent()).getQualifiedName());
  }

  public void testOpen() {
    configureCode("Belt.re", "module Option = {}");
    configureCode("Dummy.re", "open Belt.Option<caret>;");

    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    assertEquals(
        "Belt.Option", ((PsiQualifiedElement) elementAtCaret.getParent()).getQualifiedName());
    assertEquals("Belt.re", elementAtCaret.getContainingFile().getName());
  }

  public void testInclude() {
    configureCode("Css_Core.rei", "let display: string => rule");
    configureCode("Css.re", "include Css_Core<caret>; include Css_Core.Make({})");

    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    assertEquals("Css_Core", ((PsiQualifiedElement) elementAtCaret).getQualifiedName());
  }
}
