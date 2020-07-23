package com.reason.lang.napkin;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiInterpolation;
import com.reason.lang.core.psi.PsiInterpolationReference;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;

@SuppressWarnings("ConstantConditions")
public class StringTemplateParsingTest extends NsParsingTestCase {

    public void test_multilineString() {
        PsiElement e = first(letExpressions(parseCode("let _ = `this is\n a multi line\n string`"))).getBinding().getFirstChild();

        assertEquals("`this is\n a multi line\n string`", e.getText());
    }

    public void test_interpolated() {
        PsiLet e = first(letExpressions(parseCode("let _ = j`this is a ${var} Template string`")));
        PsiLetBinding binding = e.getBinding();
        PsiInterpolation inter = (PsiInterpolation) binding.getFirstChild();

        //Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, m_types.C_INTERPOLATION_PART);
        //assertSize(2, parts);
        PsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, PsiInterpolationReference.class);
        assertEquals("var", ref.getText());
    }
}
