package com.reason.lang.reason;

import com.intellij.psi.*;
import com.reason.lang.core.psi.PsiAnnotation;

import java.util.*;

public class AnnotationParsingTest extends RmlParsingTestCase {
  public void test_annotation_name() {
    assertEquals("@bs.module", ((PsiAnnotation) firstElement(parseCode("[@bs.module]"))).getName());
    assertEquals("@bs.val", ((PsiAnnotation) firstElement(parseCode("[@bs.val]"))).getName());
  }

  public void test_annotation_with_string() {
    PsiAnnotation annotation = (PsiAnnotation) firstElement(parseCode("[@bs.module \"xyz\"]"));

    assertEquals("@bs.module", annotation.getName());
  }

  public void test_chaining() {
    List<PsiNamedElement> es = new ArrayList<>(expressions(parseCode("[@bs.module \"xyz\"] [@react.component]")));

    assertSize(2, es);
    assertEquals("@bs.module", es.get(0).getName());
    assertEquals("@react.component", es.get(1).getName());
  }
}
