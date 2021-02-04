package com.reason.lang.ocaml;

import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiWhile;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class WhileParsingTest extends OclParsingTestCase {
  // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/176
  public void test_GH_176() {
    PsiLet e =
        first(letExpressions(parseCode("let x = while true do match x with | _ -> () done")));
    PsiWhile while_ = (PsiWhile) e.getBinding().getFirstChild();

    assertEquals("true", while_.getCondition().getText());
  }

  // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/189
  public void test_GH_189() {
    Collection<PsiLet> es =
        letExpressions(
            parseCode("let utf8_length s = while !p < len do () done; ()\nlet foo x = x"));
    PsiWhile while_ =
        (PsiWhile) ((PsiFunction) first(es).getBinding().getFirstChild()).getBody().getFirstChild();

    assertEquals("!p < len", while_.getCondition().getText());
    assertEquals("()", while_.getBody().getText());

    assertSize(2, es);
    assertEquals("foo", second(es).getName());
  }
}
