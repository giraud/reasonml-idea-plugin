package com.reason.lang.reason;

import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class StringTemplateParsingTest extends RmlParsingTestCase {
    public void test_basic() {
        PsiLet e = first(letExpressions(parseCode("let _ = {j|this is a $var Template string|j}")));
        PsiLetBinding binding = e.getBinding();
        PsiInterpolation inter = (PsiInterpolation) binding.getFirstChild();

        Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, m_types.C_INTERPOLATION_PART);
        assertSize(2, parts);
        PsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, PsiInterpolationReference.class);
        assertEquals("var", ref.getText());
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/353
    public void test_GH_353() {
        PsiLet e = first(letExpressions(parseCode("let _ = {j|$rowStart / $colStart|j}")));
        PsiLetBinding binding = e.getBinding();
        PsiInterpolation inter = (PsiInterpolation) binding.getFirstChild();

        List<PsiInterpolationReference> refs = ORUtil.findImmediateChildrenOfClass(inter, PsiInterpolationReference.class);
        assertSize(2, refs);
        assertEquals("rowStart", refs.get(0).getText());
        assertEquals("colStart", refs.get(1).getText());
    }
}
