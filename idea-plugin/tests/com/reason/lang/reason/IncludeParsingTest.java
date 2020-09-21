package com.reason.lang.reason;

import com.reason.lang.core.psi.PsiInclude;

public class IncludeParsingTest extends RmlParsingTestCase {
  public void test_one() {
    PsiInclude e = first(includeExpressions(parseCode("include Belt;")));

    assertEquals("Belt", e.getQualifiedName());
  }

  public void test_path() {
    PsiInclude e = first(includeExpressions(parseCode("include Belt.Array;")));

    assertEquals("Belt.Array", e.getQualifiedName());
  }

  public void test_functor() {
    PsiInclude e = first(includeExpressions(parseCode("include A.Make({ type t; })")));

    assertTrue(e.useFunctor());
    assertEquals("A.Make", e.getQualifiedName());
  }
}
