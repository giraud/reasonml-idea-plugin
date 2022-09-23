package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class StringTemplateParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        List<RPsiLet> es = letExpressions(parseCode("let x = `this is a ${var} Template string`\nlet y = 1"));

        RPsiLetBinding b = first(es).getBinding();

        assertSize(2, es);
        assertEquals("`this is a ${var} Template string`", b.getText());

        RPsiInterpolation inter = (RPsiInterpolation) b.getFirstChild();
        Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, myTypes.C_INTERPOLATION_PART);
        assertSize(2, parts);
        RPsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, RPsiInterpolationReference.class);
        assertEquals(ref.getText(), "var");
    }

    @Test
    public void test_ref_only() {
        RPsiLetBinding b = first(letExpressions(parseCode("let x = `${var}`"))).getBinding();

        assertEquals("`${var}`", b.getText());

        RPsiInterpolation inter = (RPsiInterpolation) b.getFirstChild();
        Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, myTypes.C_INTERPOLATION_PART);
        assertEmpty(parts);
        RPsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, RPsiInterpolationReference.class);
        assertEquals(ref.getText(), "var");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/353
    @Test
    public void test_GH_353() {
        RPsiLet e = first(letExpressions(parseCode("let _ = `${rowStart} / ${colStart}`")));
        RPsiLetBinding binding = e.getBinding();
        RPsiInterpolation inter = (RPsiInterpolation) binding.getFirstChild();

        List<RPsiInterpolationReference> refs = ORUtil.findImmediateChildrenOfClass(inter, RPsiInterpolationReference.class);
        assertSize(2, refs);
        assertEquals("rowStart", refs.get(0).getText());
        assertEquals("colStart", refs.get(1).getText());
    }
}
