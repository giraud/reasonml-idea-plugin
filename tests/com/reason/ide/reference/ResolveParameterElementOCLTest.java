package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.PsiParameter;

public class ResolveParameterElementOCLTest extends ORBasePlatformTestCase {
  public void test_parenLess() {
    configureCode("A.ml", "let add10 x = x<caret> + 10");

    PsiElement e = myFixture.getElementAtCaret();
    assertInstanceOf(e.getParent(), PsiParameter.class);
    assertEquals("A.add10[x]", ((PsiParameter) e.getParent()).getQualifiedName());
  }
}
