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
    public void test_basic_new() {
        List<RPsiLet> es = ORUtil.findImmediateChildrenOfClass(parseCode("let x = `this is a ${var} Template string`\nlet y = 1"), RPsiLet.class);

        RPsiLetBinding b = first(es).getBinding();

        assertSize(2, es);
        assertEquals("`this is a ${var} Template string`", b.getText());

        RPsiInterpolation inter = (RPsiInterpolation) b.getFirstChild();
        Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, myTypes.STRING_VALUE);
        assertSize(2, parts);
        RPsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, RPsiInterpolationReference.class);
        assertEquals("var", ref.getText());
    }

    @Test
    public void test_ref_only_new() {
        RPsiLetBinding b = firstOfType(parseCode("let x = `${var}`"), RPsiLet.class).getBinding();

        assertEquals("`${var}`", b.getText());

        RPsiInterpolation inter = (RPsiInterpolation) b.getFirstChild();
        Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, myTypes.C_INTERPOLATION_PART);
        assertEmpty(parts);
        RPsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, RPsiInterpolationReference.class);
        assertEquals("var", ref.getText());
    }

    @Test
    public void test_string_new() {
        RPsiInterpolation e = firstOfType(parseCode("let _ = `url(\"${var}\")`"), RPsiInterpolation.class);

        assertNoParserError(e);
        List<RPsiLiteralString> strings = ORUtil.findImmediateChildrenOfClass(e, RPsiLiteralString.class);
        assertEquals("url(\"", strings.get(0).getText());
        assertEquals("\")", strings.get(1).getText());
        RPsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(e, RPsiInterpolationReference.class);
        assertEquals("var", ref.getText());
    }

    @Test
    public void test_string_old() {
        RPsiInterpolation e = firstOfType(parseCode("let _ = j`url(\"${var}\")`"), RPsiInterpolation.class);

        assertNoParserError(e);
        List<RPsiLiteralString> strings = ORUtil.findImmediateChildrenOfClass(e, RPsiLiteralString.class);
        assertEquals("url(\"", strings.get(0).getText());
        assertEquals("\")", strings.get(1).getText());
        RPsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(e, RPsiInterpolationReference.class);
        assertEquals("var", ref.getText());
    }

    @Test
    public void test_unbalanced_new() {
        RPsiInterpolation e = firstOfType(parseCode("let _ = `url(\"${var\")`"), RPsiInterpolation.class);

        assertNoParserError(e);
        assertNull(ORUtil.findImmediateFirstChildOfClass(e, RPsiInterpolationReference.class));
        assertEquals("`url(\"${var\")`", e.getText());
    }

    @Test
    public void test_no_ref_braces() {
        RPsiInterpolation e = firstOfType(parseCode("let _ = `style {color:red}`"), RPsiInterpolation.class);

        assertNoParserError(e);
        assertNull(ORUtil.findImmediateFirstChildOfClass(e, RPsiInterpolationReference.class));
        assertNull(ORUtil.findImmediateFirstChildOfType(e, myTypes.LBRACE));
        assertNull(ORUtil.findImmediateFirstChildOfType(e, myTypes.RBRACE));
    }

    @Test
    public void test_no_ref_dollar() {
        RPsiInterpolation e = firstOfType(parseCode("let _ = `style $color:red`"), RPsiInterpolation.class);

        assertNoParserError(e);
        assertNull(ORUtil.findImmediateFirstChildOfClass(e, RPsiInterpolationReference.class));
        assertNull(ORUtil.findImmediateFirstChildOfType(e, myTypes.DOLLAR));
    }

    @Test
    public void test_no_ref_in_ref() {
        RPsiInterpolation e = firstOfType(parseCode("let _ = `test{ ${noref ${ref} }`"), RPsiInterpolation.class);

        assertNoParserError(e);
        assertEquals("ref", ORUtil.findImmediateFirstChildOfClass(e, RPsiInterpolationReference.class).getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/353
    @Test
    public void test_GH_353() {
        RPsiLet e = firstOfType(parseCode("let _ = `${rowStart} / ${colStart}`"), RPsiLet.class);
        RPsiLetBinding binding = e.getBinding();
        RPsiInterpolation inter = (RPsiInterpolation) binding.getFirstChild();

        List<RPsiInterpolationReference> refs = ORUtil.findImmediateChildrenOfClass(inter, RPsiInterpolationReference.class);
        assertSize(2, refs);
        assertEquals("rowStart", refs.get(0).getText());
        assertEquals("colStart", refs.get(1).getText());
    }
}
