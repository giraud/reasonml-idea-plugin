package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInclude;

public class IncludeParsingTest extends BaseParsingTestCase {
    public IncludeParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testOne() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));

        assertEquals("Belt", e.getQualifiedName());
    }

    public void testPath() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));

        assertEquals("Belt.Array", e.getQualifiedName());
    }

    public void testFunctor() {
        PsiInclude e = first(includeExpressions(parseCode("include A.Make(struct type t end)")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getQualifiedName());
    }

    public void testWithType() {
        PsiInclude e = first(includeExpressions(parseCode("include Grammar.S with type te = Tok.t and type 'c pattern = 'c Tok.p\ntype t", true))); // Coq: pcoq.ml

        assertEquals("Grammar.S", e.getQualifiedName());
        assertEquals("include Grammar.S with type te = Tok.t and type 'c pattern = 'c Tok.p", e.getText());
    }


}
