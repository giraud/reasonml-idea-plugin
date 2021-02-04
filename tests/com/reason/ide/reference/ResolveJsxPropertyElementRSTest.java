package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameter;

@SuppressWarnings("ConstantConditions")
public class ResolveJsxPropertyElementRSTest extends ORBasePlatformTestCase {

  public void test_basic() {
    configureCode("X.res", "let make = ~value => <div/>;");
    configureCode("A.res", "<X value<caret> =1;");

    PsiElement e = myFixture.getElementAtCaret();
    assertInstanceOf(e.getParent(), PsiParameter.class);
    assertEquals("X.make[value]", ((PsiParameter) e.getParent()).getQualifiedName());
  }

  public void test_basic_multiple_props() {
    configureCode("X.res", "let make = (~propA, ~propB, ~propC) => <div/>;");
    configureCode("A.res", "<X propA propB propC<caret> />;");

    PsiElement e = myFixture.getElementAtCaret();
    assertInstanceOf(e.getParent(), PsiParameter.class);
    assertEquals("X.make[propC]", ((PsiParameter) e.getParent()).getQualifiedName());
  }

  public void test_basic_nested() {
    configureCode("A.res", "module X = { let make = ~value => <div/>; }; <X value<caret>=1;");

    PsiElement e = myFixture.getElementAtCaret();
    assertInstanceOf(e.getParent(), PsiParameter.class);
    assertEquals("value", e.getText());
  }

  public void test_autoclose() {
    configureCode("X.re", "let make = ~value => <div/>;");
    configureCode("A.re", "<X value<caret> =1/>;");

    PsiElement e = myFixture.getElementAtCaret();
    assertInstanceOf(e.getParent(), PsiParameter.class);
    assertEquals("X.make[value]", ((PsiParameter) e.getParent()).getQualifiedName());
  }

  public void test_open() {
    configureCode("A.res", "module X = { let make = ~value => <div/>; };");
    configureCode("B.res", "open A; <X value<caret>=1;");

    PsiElement e = myFixture.getElementAtCaret();
    assertInstanceOf(e.getParent(), PsiParameter.class);
    assertEquals("value", e.getText());
  }

  public void test_multiple() {
    configureCode(
        "A.res",
        "module X = { let make = ~value => <div/>; }; module Y = { let make = ~value => <div/>; }; ");
    configureCode("B.res", "open A; <X value<caret>=1;");

    PsiElement e = myFixture.getElementAtCaret();
    assertInstanceOf(e.getParent(), PsiParameter.class);
    assertEquals("A.X.make", PsiTreeUtil.getParentOfType(e, PsiLet.class).getQualifiedName());
  }

  public void test_make_make() {
    configureCode(
        "A.re",
        "module X = { let make = ~value => <div/>; }; let make = ~value => <X value<caret> = 1>; ");

    PsiElement e = myFixture.getElementAtCaret();
    assertInstanceOf(e.getParent(), PsiParameter.class);
    assertEquals("A.X.make", PsiTreeUtil.getParentOfType(e, PsiLet.class).getQualifiedName());
  }
}
