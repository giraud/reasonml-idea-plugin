package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class StringTemplateParsingTest extends ResParsingTestCase {
    public void test_basic() {
        List<PsiLet> es = letExpressions(parseCode("let x = `this is a ${var} Template string`\nlet y = 1"));

        PsiLetBinding b = first(es).getBinding();

        assertSize(2, es);
        assertEquals("`this is a ${var} Template string`", b.getText());

        PsiInterpolation inter = (PsiInterpolation) b.getFirstChild();
        Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, m_types.C_INTERPOLATION_PART);
        assertSize(2, parts);
        PsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, PsiInterpolationReference.class);
        assertEquals(ref.getText(), "var");
    }

    public void test_ref_only() {
        PsiLetBinding b = first(letExpressions(parseCode("let x = `${var}`"))).getBinding();

        assertEquals("`${var}`", b.getText());

        PsiInterpolation inter = (PsiInterpolation) b.getFirstChild();
        Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, m_types.C_INTERPOLATION_PART);
        assertEmpty(parts);
        PsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, PsiInterpolationReference.class);
        assertEquals(ref.getText(), "var");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/353
    public void test_GH_353() {
        PsiLet e = first(letExpressions(parseCode("let _ = `${rowStart} / ${colStart}`")));
        PsiLetBinding binding = e.getBinding();
        PsiInterpolation inter = (PsiInterpolation) binding.getFirstChild();

        List<PsiInterpolationReference> refs = ORUtil.findImmediateChildrenOfClass(inter, PsiInterpolationReference.class);
        assertSize(2, refs);
        assertEquals("rowStart", refs.get(0).getText());
        assertEquals("colStart", refs.get(1).getText());
    }
}
