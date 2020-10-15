package com.reason.lang.ocaml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiPatternMatch;
import java.util.*;

public class PolyVariantTest extends OclParsingTestCase {
  public void test_basicLIdent() {
    PsiLet e = first(letExpressions(parseCode("let x = `red")));
    PsiElement variant =
        first(ORUtil.findImmediateChildrenOfType(e.getBinding(), m_types.POLY_VARIANT));

    assertEquals("`red", variant.getText());
  }

  public void test_basicUIdent() {
    PsiLet e = first(letExpressions(parseCode("let x = `Red")));
    PsiElement variant =
        first(ORUtil.findImmediateChildrenOfType(e.getBinding(), m_types.POLY_VARIANT));

    assertEquals("`Red", variant.getText());
  }

  public void test_patternMatchConstant() {
    PsiFile psiFile =
        parseCode(
            "let unwrapValue = fun | `String s -> toJsUnsafe s | `bool b -> toJsUnsafe (Js.Boolean.to_js_boolean b)");

    Collection<PsiNamedElement> expressions = expressions(psiFile);
    assertEquals(1, expressions.size());

    Collection<PsiPatternMatch> matches =
        PsiTreeUtil.findChildrenOfType(first(expressions), PsiPatternMatch.class);
    assertEquals(2, matches.size());
  }
}
