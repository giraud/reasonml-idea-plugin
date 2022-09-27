package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.impl.RPsiAnnotation;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class AnnotationParsingTest extends ResParsingTestCase {
    @Test
    public void test_annotation_name() {
        assertEquals("@bs.module", ((RPsiAnnotation) firstElement(parseCode("@bs.module"))).getName());
        assertEquals("@bs.val", ((RPsiAnnotation) firstElement(parseCode("@bs.val"))).getName());
    }

    @Test
    public void test_annotation_with_string() {
        RPsiAnnotation annotation = (RPsiAnnotation) firstElement(parseCode("@module(\"xyz\")"));

        assertEquals("@module", annotation.getName());
    }

    @Test
    public void test_chaining() {
        List<PsiNamedElement> es = new ArrayList<>(expressions(parseCode("@module(\"xyz\") @react.component")));

        assertSize(2, es);
        assertEquals("@module", es.get(0).getName());
        assertEquals("@react.component", es.get(1).getName());
    }

    @Test
    public void test_eol() {
        RPsiType e = firstOfType(parseCode("type t = { @optional\n fn: unit => string }"), RPsiType.class);

        RPsiRecord r = (RPsiRecord) e.getBinding().getFirstChild();
        RPsiAnnotation a = PsiTreeUtil.findChildOfType(r, RPsiAnnotation.class);
        assertEquals("@optional", a.getName());
        assertEquals("fn", r.getFields().get(0).getName());
    }

    @Test
    public void test_doc() {
        RPsiAnnotation e = firstOfType(parseCode("@ocaml.doc(\n \"something\" \n )"), RPsiAnnotation.class);

        assertEquals("@ocaml.doc", e.getName());
        assertEquals("\"something\"", e.getValue().getText());
    }

    @Test
    public void test_uncurry() {
        RPsiAnnotation e = firstOfType(parseCode("type t = @uncurry unit => unit"), RPsiAnnotation.class);

        assertEquals("@uncurry", e.getText());
    }
}
