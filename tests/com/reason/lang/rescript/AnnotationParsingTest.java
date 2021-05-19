package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class AnnotationParsingTest extends ResParsingTestCase {
    public void test_annotation_name() {
        assertEquals("@bs.module", ((PsiAnnotation) firstElement(parseCode("@bs.module"))).getName());
        assertEquals("@bs.val", ((PsiAnnotation) firstElement(parseCode("@bs.val"))).getName());
    }

    public void test_annotation_with_string() {
        PsiAnnotation annotation = (PsiAnnotation) firstElement(parseCode("@module(\"xyz\")"));

        assertEquals("@module", annotation.getName());
    }

    public void test_chaining() {
        List<PsiNamedElement> es = new ArrayList<>(expressions(parseCode("@module(\"xyz\") @react.component")));

        assertSize(2, es);
        assertEquals("@module", es.get(0).getName());
        assertEquals("@react.component", es.get(1).getName());
    }

    public void test_eol() {
        PsiType e = firstOfType(parseCode("type t = { @optional\n fn: unit => string }"), PsiType.class);

        PsiRecord r = (PsiRecord) e.getBinding().getFirstChild();
        PsiAnnotation a = PsiTreeUtil.findChildOfType(r, PsiAnnotation.class);
        assertEquals("@optional", a.getName());
        assertEquals("fn", r.getFields().get(0).getName());
    }
}
