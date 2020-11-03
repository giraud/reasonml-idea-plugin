package com.reason.lang.ocaml;

import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiScopedExpr;
import com.reason.lang.core.psi.impl.PsiValImpl;

public class ValParsingTest extends OclParsingTestCase {
  public void test_qualifiedName() {
    PsiVal e = first(valExpressions(parseCode("val x : int")));

    assertEquals("Dummy.x", e.getQualifiedName());
    assertFalse(e.isFunction());
  }

  public void test_name() {
    PsiVal e = first(valExpressions(parseCode("val x : int")));

    assertInstanceOf(((PsiValImpl) e).getNameIdentifier(), PsiLowerIdentifier.class);
    assertEquals("x", e.getName());
  }

  public void test_specialName() {
    PsiVal e = first(valExpressions(parseCode("val (>>=) : 'a -> 'a t")));

    assertInstanceOf(((PsiValImpl) e).getNameIdentifier(), PsiScopedExpr.class);
    assertEquals("(>>=)", e.getName());
  }

  public void test_function() {
    PsiVal e = first(valExpressions(parseCode("val x : 'a -> 'a t")));

    assertTrue(e.isFunction());
  }
}
