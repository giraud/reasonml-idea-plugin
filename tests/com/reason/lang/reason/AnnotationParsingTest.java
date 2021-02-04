package com.reason.lang.reason;

import com.reason.lang.core.psi.PsiAnnotation;

public class AnnotationParsingTest extends RmlParsingTestCase {
  public void test_annotationName() {
    assertEquals("@bs.module", ((PsiAnnotation) firstElement(parseCode("[@bs.module]"))).getName());
    assertEquals("@bs.val", ((PsiAnnotation) firstElement(parseCode("[@bs.val]"))).getName());
  }

  public void test_annotationWithString() {
    PsiAnnotation annotation = (PsiAnnotation) firstElement(parseCode("[@bs.module \"xyz\"]"));

    assertEquals("@bs.module", annotation.getName());
  }
}
