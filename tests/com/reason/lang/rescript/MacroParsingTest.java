package com.reason.lang.rescript;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiMacro;
import com.reason.lang.core.psi.impl.PsiMacroName;
import com.reason.lang.core.psi.impl.PsiRawBody;

@SuppressWarnings("ConstantConditions")
public class MacroParsingTest extends ResParsingTestCase {
  public void test_basic() {
    PsiLet expression = first(letExpressions(parseCode("let _ = %raw(\"xxx\")")));

    PsiElement macro = expression.getBinding().getFirstChild();
    assertInstanceOf(macro, PsiMacro.class);

    PsiRawBody rawMacroBody = PsiTreeUtil.findChildOfType(macro, PsiRawBody.class);
    assertEquals("raw", PsiTreeUtil.findChildOfType(macro, PsiMacroName.class).getText());
    assertEquals("(\"xxx\")", rawMacroBody.getText());
  }

  // public void test_rootRaw() {
  //    PsiElement e = firstElement(parseCode("%raw(\"xxx\")"));
  //
  //    assertInstanceOf(e, PsiRaw.class);
  //    PsiRawBody rawBody = PsiTreeUtil.findChildOfType(e, PsiRawBody.class);
  //    assertEquals("\"xxx\"", rawBody.getText());
  // }

  // public void testMultiLine() {
  //    PsiLet expression = first(letExpressions(parseCode("let _ = %raw({|function (a) {}|})")));
  //
  //    PsiElement macro = expression.getBinding().getFirstChild();
  //    assertInstanceOf(macro, PsiMacro.class);
  //
  //    PsiRawBody rawMacroBody = PsiTreeUtil.findChildOfType(macro, PsiRawBody.class);
  //    assertEquals("%raw", PsiTreeUtil.findChildOfType(macro, PsiMacroName.class).toString());
  //    assertEquals("{|function (a) {}|}", rawMacroBody.getText());
  //    assertEquals(new TextRange(2, 17), rawMacroBody.getMacroTextRange());
  // }
}
