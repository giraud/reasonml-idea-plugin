package com.reason.lang.ocaml;

import com.reason.lang.core.psi.*;

public class IncludeParsingTest extends OclParsingTestCase {
    public void test_one() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));

        assertEquals("Belt", e.getPath());
    }

    public void test_path() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));

        assertEquals("Belt.Array", e.getPath());
    }

    public void test_functor() {
        PsiInclude e = first(includeExpressions(parseCode("include A.Make(struct type t end)")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getPath());
    }

    public void test_withType() {
        PsiInclude e = first(includeExpressions(parseCode("include S with type t = Tok.t")));

        assertEquals("S", e.getPath());
        assertEquals("include S with type t = Tok.t", e.getText());
    }

    public void test_withPathType() {
        PsiInclude e = first(includeExpressions(parseCode("include Grammar.S with type te = Tok.t and type 'c pattern = 'c Tok.p\ntype t"))); // Coq: pcoq.ml

        assertEquals("Grammar.S", e.getPath());
        assertEquals("include Grammar.S with type te = Tok.t and type 'c pattern = 'c Tok.p", e.getText());
    }
}
