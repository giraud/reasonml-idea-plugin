package com.reason.lang.ocaml;

import com.reason.lang.core.psi.PsiOpen;

public class OpenParsingTest extends OclParsingTestCase {
  public void test_one() {
    PsiOpen e = first(openExpressions(parseCode("open Belt")));

    assertEquals("Belt", e.getQualifiedName());
  }

  public void test_path() {
    PsiOpen e = first(openExpressions(parseCode("open Belt.Array")));

    assertEquals("Belt.Array", e.getQualifiedName());
  }

  public void test_chaining() {
    PsiOpen e = first(openExpressions(parseCode("open Belt Array")));

    assertEquals("Belt", e.getQualifiedName());
  }

  public void test_functor() {
    PsiOpen e = first(openExpressions(parseCode("open A.Make(struct type t end)")));

    assertEquals(true, e.useFunctor());
    assertEquals("A.Make", e.getQualifiedName());
  }
}
