package com.reason.ide.structure;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiVal;

@SuppressWarnings("ConstantConditions")
public class ValPresentationTest extends ORBasePlatformTestCase {

  public void test_noSig() {
    PsiVal e = configureCode("A.ml", "val x = 1").getExpressions("A.x", PsiVal.class).get(0);

    assertEquals("x", e.getPresentation().getPresentableText());
    assertNull(e.getPresentation().getLocationString());
  }

  public void test_sig() {
    PsiVal e =
        configureCode("A.mli", "val x : 'a -> 'a t").getExpressions("A.x", PsiVal.class).get(0);

    assertEquals("x", e.getPresentation().getPresentableText());
    assertEquals("'a -> 'a t", e.getPresentation().getLocationString());
  }
}
