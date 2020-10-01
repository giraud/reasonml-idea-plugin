package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.impl.PsiIfStatement;
import java.util.ArrayList;
import java.util.List;

public class IfParsingTest extends OclParsingTestCase {
  public void test_basicIf() {
    PsiFile psiFile = parseCode("let _ = if x then ()");
    PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

    assertNotNull(e);
    assertNotNull(e.getCondition());
    PsiScopedExpr ifScope = PsiTreeUtil.findChildOfType(e, PsiScopedExpr.class);
    assertNotNull(ifScope);
    assertEquals("()", ifScope.getText());
  }

  public void test_basicIfElse() {
    PsiFile psiFile = parseCode("let _ = if x then 1 else 2");
    PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

    assertNotNull(e);
    assertNotNull(e.getCondition());
    List<PsiScopedExpr> scopes =
        new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiScopedExpr.class));
    assertEquals(2, scopes.size());
    assertEquals("1", scopes.get(0).getText());
    assertEquals("2", scopes.get(1).getText());
  }

  public void test_ifWithIn() {
    PsiFile file = parseCode("let _ =  if x then let init = y in let data = z");

    assertEquals(1, letExpressions(file).size());
    assertNotNull(firstOfType(file, PsiIfStatement.class));
  }
}
