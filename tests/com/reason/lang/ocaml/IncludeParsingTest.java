package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class IncludeParsingTest extends OclParsingTestCase {
    public void test_one() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));

        assertNull(PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class));
        assertEquals("Belt", e.getIncludePath());
        assertEquals("Belt", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    public void test_path() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));

        assertEquals("Belt.Array", e.getIncludePath());
        assertEquals("Array", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }


    public void test_functor() {
        PsiInclude e = firstOfType(parseCode("include Make(struct type t end)"), PsiInclude.class);

        assertTrue(e.useFunctor());
        PsiFunctorCall c = PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class);
        assertEquals("Make", c.getName());
        assertEquals(myTypes.A_MODULE_NAME, c.getNavigationElement().getNode().getElementType());
        assertEquals("Make", e.getIncludePath());
    }

    public void test_functor_path() {
        PsiInclude e = firstOfType(parseCode("include A.Make(struct type t end)"), PsiInclude.class);

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getIncludePath());
    }

    public void test_with_type() {
        PsiInclude e = first(includeExpressions(parseCode("include S with type t = Tok.t")));

        assertEquals("S", e.getIncludePath());
        assertEquals("include S with type t = Tok.t", e.getText());
    }

    public void test_with_path_type() {
        PsiInclude e = first(includeExpressions(parseCode("include Grammar.S with type te = Tok.t and type 'c pattern = 'c Tok.p\ntype t"))); // Coq: pcoq.ml

        assertEquals("Grammar.S", e.getIncludePath());
        assertEquals("include Grammar.S with type te = Tok.t and type 'c pattern = 'c Tok.p", e.getText());
    }
}
