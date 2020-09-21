package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiIfStatement;
import com.reason.lang.core.psi.PsiScopedExpr;
import java.util.*;

public class IfParsingTest extends RmlParsingTestCase {
  public void test_basicIfParsing() {
    PsiFile psiFile = parseCode("if (x) { (); }");
    PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

    assertNotNull(e);
    assertNotNull(e.getBinaryCondition());
    assertEquals("(x)", e.getBinaryCondition().getText());
    PsiScopedExpr ifScope = PsiTreeUtil.findChildOfType(e, PsiScopedExpr.class);
    assertNotNull(ifScope);
    assertEquals("{ (); }", ifScope.getText());
  }

  public void test_basicIfElseNoBraceParsing() {
    PsiFile psiFile = parseCode("let test = x => if (x) 1 else 2;");
    PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

    assertNotNull(e);
    assertNotNull(e.getBinaryCondition());
    List<PsiScopedExpr> scopes =
        new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiScopedExpr.class));
    // zzz
    // assertEquals(2, scopes.size());
    // assertEquals("1", scopes.get(0).getText());
    // assertEquals("2", scopes.get(1).getText());
  }

  public void test_basicIfElseParsing() {
    PsiFile psiFile = parseCode("let test = x => if (x) { 1; } else { 2; };");
    PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

    assertNotNull(e);
    assertNotNull(e.getBinaryCondition());
    List<PsiScopedExpr> scopes =
        new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiScopedExpr.class));
    assertEquals(2, scopes.size());
    assertEquals("{ 1; }", scopes.get(0).getText());
    assertEquals("{ 2; }", scopes.get(1).getText());
  }
}
