package com.reason.lang.reason;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class AnnotationParsingTest extends RmlParsingTestCase {
    @Test
    public void test_annotation_name() {
        assertEquals("@bs.module", firstOfType(parseCode("[@bs.module]"), RPsiAnnotation.class).getName());
        assertEquals("@bs.val", firstOfType(parseCode("[@bs.val]"), RPsiAnnotation.class).getName());
    }

    @Test
    public void test_annotation_with_string() {
        RPsiAnnotation annotation = firstOfType(parseCode("[@bs.module \"xyz\"]"), RPsiAnnotation.class);

        assertEquals("@bs.module", annotation.getName());
    }

    @Test
    public void test_chaining() {
        List<PsiNamedElement> es = new ArrayList<>(expressions(parseCode("[@bs.module \"xyz\"] [@react.component]")));

        assertSize(2, es);
        assertEquals("@bs.module", es.get(0).getName());
        assertEquals("@react.component", es.get(1).getName());
    }
}
