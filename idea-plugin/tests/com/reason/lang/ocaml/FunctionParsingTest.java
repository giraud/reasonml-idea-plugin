package com.reason.lang.ocaml;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctionParsingTest extends OclParsingTestCase {
  public void test_single_param() {
    PsiLet e = first(letExpressions(parseCode("let fn x = x")));

    assertTrue(e.isFunction());
    PsiFunction function = e.getFunction();
    assertSize(1, function.getParameters());
    assertInstanceOf(first(function.getParameters()).getNameIdentifier(), PsiLowerIdentifier.class);
    assertNotNull(function.getBody());
  }

  public void test_multiple_params() {
    PsiLet e = first(letExpressions(parseCode("let add x y = x + y")));

    assertTrue(e.isFunction());
    PsiFunction function = e.getFunction();
    assertSize(2, function.getParameters());
    assertInstanceOf(first(function.getParameters()).getNameIdentifier(), PsiLowerIdentifier.class);
    assertInstanceOf(
        second(function.getParameters()).getNameIdentifier(), PsiLowerIdentifier.class);
    assertNotNull(function.getBody());
  }

  public void test_letBinding() {
    PsiLet e =
        first(letExpressions(parseCode("let getAttributes node = let attr = \"r\" in attr")));

    assertTrue(e.isFunction());
    PsiFunction function = e.getFunction();
    assertSize(1, function.getParameters());
    assertNotNull(function.getBody());
  }

  public void test_letBinding2() {
    PsiLet e =
        first(
            letExpressions(parseCode("let visit_vo f = Printf.printf \"a\"; Printf.printf \"b\"")));

    assertTrue(e.isFunction());
    PsiFunction function = e.getFunction();
    assertEquals("Printf.printf \"a\"; Printf.printf \"b\"", function.getBody().getText());
  }

  public void test_fun() {
    PsiLet e = first(letExpressions(parseCode("let _ = fun (_, info as ei) -> x")));

    assertTrue(e.isFunction());
    PsiFunction function = e.getFunction();
    assertEquals(
        "(_, info as ei)", PsiTreeUtil.findChildOfType(function, PsiParameters.class).getText());
    assertEquals("x", function.getBody().getText());
  }

  public void test_fun_signature() {
    PsiLet e = first(letExpressions(parseCode("let _: int -> int = fun x y -> x + y")));

    assertTrue(e.isFunction());
    assertEquals("fun x y -> x + y", e.getBinding().getText());
    PsiFunction f = e.getFunction();
    List<PsiParameter> p = f.getParameters();
    assertSize(2, p);
    assertEquals("x", p.get(0).getText());
    assertEquals("y", p.get(1).getText());
    assertEquals("x + y", f.getBody().getText());
  }

  public void test_complexParams() {
    Collection<PsiNamedElement> expressions =
        expressions(
            parseCode("let resolve_morphism env ?(fnewt=fun x -> x) args' (b,cstr) = let x = 1"));

    assertSize(1, expressions);
    PsiLet let = (PsiLet) first(expressions);
    assertTrue(let.isFunction());
    assertSize(4, let.getFunction().getParameters());
    assertEquals("let x = 1", let.getFunction().getBody().getText());
  }
}
