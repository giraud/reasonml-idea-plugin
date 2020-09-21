package com.reason.lang.napkin;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiIfStatement;
import com.reason.lang.core.psi.PsiScopedExpr;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class IfParsingTest extends NsParsingTestCase {
  public void test_basic() {
    PsiIfStatement e = firstOfType(parseCode("if x { () }"), PsiIfStatement.class);

    assertNotNull(e);
    assertEquals("x", e.getBinaryCondition().getText());
    PsiScopedExpr ifScope = PsiTreeUtil.findChildOfType(e, PsiScopedExpr.class);
    assertNotNull(ifScope);
    assertEquals("{ () }", ifScope.getText());
  }

  public void test_basicIfElse() {
    PsiFile psiFile = parseCode("let test = x => if x { 1 } else { 2 }");
    PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

    assertNotNull(e);
    assertNotNull(e.getBinaryCondition());
    List<PsiScopedExpr> scopes =
        new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiScopedExpr.class));
    assertEquals(2, scopes.size());
    assertEquals("{ 1 }", scopes.get(0).getText());
    assertEquals("{ 2 }", scopes.get(1).getText());
  }

  public void test_ifElseNoBrace() {
    PsiIfStatement e =
        firstOfType(parseCode("let test = x => if x 1 else 2"), PsiIfStatement.class);

    assertNotNull(e);
    assertNotNull(e.getBinaryCondition());
    List<PsiScopedExpr> scopes =
        new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiScopedExpr.class));
    // zzz
    // assertEquals(2, scopes.size());
    // assertEquals("1", scopes.get(0).getText());
    // assertEquals("2", scopes.get(1).getText());
  }
}
