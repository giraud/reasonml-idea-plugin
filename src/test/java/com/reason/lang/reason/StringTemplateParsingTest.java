package com.reason.lang.reason;

import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class StringTemplateParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        RPsiLet e = firstOfType(parseCode("let _ = {j|this is a $var Template string|j}"), RPsiLet.class);
        RPsiLetBinding binding = e.getBinding();
        RPsiInterpolation inter = (RPsiInterpolation) binding.getFirstChild();

        Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, myTypes.C_INTERPOLATION_PART);
        assertSize(2, parts);
        RPsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, RPsiInterpolationReference.class);
        assertEquals("var", ref.getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/353
    @Test
    public void test_GH_353() {
        RPsiLet e = firstOfType(parseCode("let _ = {j|$rowStart / $colStart|j}"), RPsiLet.class);
        RPsiLetBinding binding = e.getBinding();
        RPsiInterpolation inter = (RPsiInterpolation) binding.getFirstChild();

        List<RPsiInterpolationReference> refs = ORUtil.findImmediateChildrenOfClass(inter, RPsiInterpolationReference.class);
        assertSize(2, refs);
        assertEquals("rowStart", refs.get(0).getText());
        assertEquals("colStart", refs.get(1).getText());
    }
}
