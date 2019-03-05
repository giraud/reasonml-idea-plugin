package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiStruct;

import java.util.Collection;

public class FunctorTest extends BaseParsingTestCase {
    public FunctorTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testBasic() {
        PsiNamedElement e = first(expressions(parseCode("module Make (M:Def) : S = struct end")));

        assertNotNull(e);
        assertInstanceOf(e, PsiFunctor.class);
        assertEquals("struct end", ((PsiFunctor) e).getBinding().getText());
    }

    public void testModuleFunctor2() {
        Collection<PsiNamedElement> expressions = expressions(parseCode("module Make (M: Input) : S with type +'a t = 'a M.t = struct end"));

        assertEquals(1, expressions.size());
        PsiFunctor functor = (PsiFunctor) first(expressions);

        assertEquals("(M: Input)", functor.getParameters().getText());
        assertEquals("struct end", functor.getBinding().getText());
    }

    public void testModuleFunctorInstanciation1() {
        PsiInnerModule module = first(moduleExpressions(parseCode("module Printing = Make (struct let encode = encode_record end)")));
        PsiStruct struct = PsiTreeUtil.findChildOfType(module.getBody(), PsiStruct.class);

        assertNotNull(struct);
    }

    public void testModuleFunctorInstantiation2() {
        PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash)\ntype infos");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(2, expressions.size());
    }

}
