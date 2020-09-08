package com.reason.lang.napkin;

import java.util.*;
import com.intellij.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiAnnotation;

public class AnnotationParsingTest extends NsParsingTestCase {
    public void test_withString() {
        assertEquals("@bs.module", ((PsiAnnotation) firstElement(parseCode("@bs.module(\"xyz\")"))).getName());
    }

    public void test_chaining() {
        List<PsiNamedElement> es = new ArrayList<>(expressions(parseCode("@bs.module(\"xyz\") @react.component")));

        assertSize(2, es);
        assertEquals("@bs.module", es.get(0).getName());
        assertEquals("@react.component", es.get(1).getName());
    }

    public void test_name() {
        assertEquals("@bs.module", ((PsiAnnotation) firstElement(parseCode("@bs.module"))).getName());
        assertEquals("@bs.val", ((PsiAnnotation) firstElement(parseCode("@bs.val"))).getName());
    }
}
