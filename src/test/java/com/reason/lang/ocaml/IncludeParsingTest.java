package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class IncludeParsingTest extends OclParsingTestCase {
    @Test
    public void test_one() {
        RPsiInclude e = firstOfType(parseCode("include Belt"), RPsiInclude.class);

        assertNull(PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class));
        assertEquals("Belt", e.getIncludePath());
        assertEquals("Belt", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_path() {
        RPsiInclude e = firstOfType(parseCode("include Belt.Array"), RPsiInclude.class);

        assertEquals("Belt.Array", e.getIncludePath());
        assertEquals("Array", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_functor() {
        RPsiInclude e = firstOfType(parseCode("include Make(struct type t end)"), RPsiInclude.class);

        assertTrue(e.useFunctor());
        RPsiFunctorCall c = PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class);
        assertEquals("Make", c.getName());
        assertEquals(myTypes.A_MODULE_NAME, c.getReferenceIdentifier().getNode().getElementType());
        assertEquals("Make", e.getIncludePath());
    }

    @Test
    public void test_functor_path() {
        RPsiInclude e = firstOfType(parseCode("include A.Make(struct type t end)"), RPsiInclude.class);

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getIncludePath());
    }

    @Test
    public void test_with_type() {
        RPsiInclude e = firstOfType(parseCode("include S with type t = Tok.t"), RPsiInclude.class);

        assertEquals("S", e.getIncludePath());
        assertEquals("include S with type t = Tok.t", e.getText());
    }

    @Test
    public void test_with_path_type() {
        RPsiInclude e = firstOfType(parseCode("include Grammar.S with type te = Tok.t and type 'c pattern = 'c Tok.p\ntype t"), RPsiInclude.class); // Coq: pcoq.ml

        assertEquals("Grammar.S", e.getIncludePath());
        assertEquals("include Grammar.S with type te = Tok.t and type 'c pattern = 'c Tok.p", e.getText());
    }

    @Test
    public void test_GH_497_include_inlined_module() {
        RPsiInclude e = firstOfType(parseCode("include module type of struct include Env.Path end"), RPsiInclude.class); // Coq: boot/path.mli

        assertEmpty(e.getIncludePath());
        assertNotNull(PsiTreeUtil.findChildOfType(e, RPsiModule.class));
    }
}
