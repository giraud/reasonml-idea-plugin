package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;

public class ResolveDeconstructionTest extends ORBasePlatformTestCase {

  public void test_Ns_GH_167() {
    configureCode(
        "A.res", "let (count, setCount) = React.useState(() => 0);\n" + "setCount<caret>(1);");

    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    assertEquals(12, elementAtCaret.getTextOffset());
  }

  public void test_Rml_GH_167() {
    configureCode(
        "A.re", "let (count, setCount) = React.useState(() => 0);\n" + "setCount<caret>(1);");

    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    assertEquals(12, elementAtCaret.getTextOffset());
  }
}
