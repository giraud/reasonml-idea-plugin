package com.reason.lang.napkin;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.impl.PsiInterpolation;
import com.reason.lang.core.psi.impl.PsiInterpolationReference;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class StringTemplateParsingTest extends NsParsingTestCase {

  public void test_multilineString() {
    PsiElement e =
        first(letExpressions(parseCode("let _ = `this is\n a multi line\n string`")))
            .getBinding()
            .getFirstChild();

    assertEquals("`this is\n a multi line\n string`", e.getText());
  }

  public void test_interpolated() {
    PsiLet e = first(letExpressions(parseCode("let _ = j`this is a ${var} Template string`")));
    PsiLetBinding binding = e.getBinding();
    PsiInterpolation inter = (PsiInterpolation) binding.getFirstChild();

    List<PsiElement> parts =
        (List<PsiElement>) ORUtil.findImmediateChildrenOfType(inter, m_types.C_INTERPOLATION_PART);
    assertSize(2, parts);
    assertEquals("this is a", parts.get(0).getText());
    assertEquals("Template string", parts.get(1).getText());
    PsiInterpolationReference ref =
        ORUtil.findImmediateFirstChildOfClass(inter, PsiInterpolationReference.class);
    assertEquals("var", ref.getText());
  }
}
