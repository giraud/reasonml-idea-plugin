package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;

import java.util.Collection;

public class FunctorTest extends BaseParsingTestCase {
    public FunctorTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiNamedElement e = first(expressions(parseCode("module Make = (M: Def) : S => {};")));

        assertNotNull(e);
        assertInstanceOf(e, PsiFunctor.class);
    }

    public void testModuleFunctor2() {
        Collection<PsiNamedElement> expressions = expressions(parseCode("module Make = (M: Input) : (S with type input = M.t) => {};"));

        assertEquals(1, expressions.size());
        PsiFunctor functor = (PsiFunctor) first(expressions);

        assertEquals("(M: Input)", functor.getParameters().getText());
        assertEquals("{}", functor.getBinding().getText());
    }

    public void testModuleFunctorInstanciation1() {
        PsiModule module = first(moduleExpressions(parseCode("module Printing = Make({ let encode = encode_record; });")));

        assertNotNull(module.getBody());
    }

    public void testModuleFunctorInstantiation2() {
        PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash);\ntype infos;");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(2, expressions.size());
    }
}
