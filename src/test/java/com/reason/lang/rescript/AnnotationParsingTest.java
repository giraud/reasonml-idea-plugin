package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class AnnotationParsingTest extends ResParsingTestCase {
    @Test
    public void test_annotation_name() {
        assertEquals("@bs.module", firstOfType(parseCode("@bs.module"), RPsiAnnotation.class).getName());
        assertEquals("@bs.val", firstOfType(parseCode("@bs.val"), RPsiAnnotation.class).getName());
    }

    @Test
    public void test_annotation_with_string() {
        RPsiAnnotation annotation = firstOfType(parseCode("@module(\"xyz\")"), RPsiAnnotation.class);

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
        RPsiType e = firstOfType(parseCode("""
                type t = {
                  @optional
                  fn: unit => string }
                """), RPsiType.class);
        assertNoParserError(e);

        RPsiRecord r = (RPsiRecord) e.getBinding().getFirstChild();
        RPsiAnnotation a = PsiTreeUtil.findChildOfType(r, RPsiAnnotation.class);
        assertEquals("@optional", a.getName());
        assertEquals("fn", r.getFields().get(0).getName());
    }

    @Test
    public void test_doc() {
        RPsiAnnotation e = firstOfType(parseCode("""
                @ocaml.doc(
                  "something"
                )
                """), RPsiAnnotation.class);

        assertEquals("@ocaml.doc", e.getName());
        assertEquals("\"something\"", e.getValue().getText());
    }

    @Test
    public void test_uncurry() {
        RPsiAnnotation e = firstOfType(parseCode("type t = @uncurry unit => unit"), RPsiAnnotation.class);

        assertEquals("@uncurry", e.getText());
    }
}
