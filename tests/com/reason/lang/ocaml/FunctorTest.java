package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiStruct;

import java.util.Collection;

public class FunctorTest extends BaseParsingTestCase {
    public FunctorTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testBasic() {
        PsiNameIdentifierOwner e = first(expressions(parseCode("module Make (M:Def) : S = struct end")));

        assertNotNull(e);
        assertInstanceOf(e, PsiFunctor.class);
        assertEquals("struct end", ((PsiFunctor) e).getBinding().getText());
    }

    public void testModuleFunctor2() {
        Collection<PsiNameIdentifierOwner> expressions = expressions(parseCode("module Make (M: Input) : S with type +'a t = 'a M.t = struct end"));

        assertEquals(1, expressions.size());
        PsiFunctor functor = (PsiFunctor) first(expressions);

        assertEquals("M: Input", first(functor.getParameters()).getText());
        //assertEquals("S with type +'a t = 'a M.t", functor.getReturnType().getText());
        assertEquals("struct end", functor.getBinding().getText());
    }

    public void testSignature() {
        Collection<PsiFunctor> functors = functorExpressions(parseCode(
                "module GlobalBindings (M : sig\n" +
                        "  val relation_classes : string list\n" +
                        "  val morphisms : string list\n" +
                        "  val arrow : evars -> evars * constr\n" +
                        "end) = struct\n" +
                        "  open M\n" +
                        "end"));

        assertEquals(1, functors.size());
        PsiFunctor functor = first(functors);
        assertEquals("GlobalBindings", functor.getName());
        assertEquals("Dummy.GlobalBindings", functor.getQualifiedName());
        Collection<PsiParameter> parameters = functor.getParameters();
        assertSize(1, parameters);
        assertEquals("M", first(parameters).getName());
        assertNotNull(functor.getBinding());
    }

    public void testModuleFunctorInstanciation1() {
        PsiInnerModule module = first(moduleExpressions(parseCode("module Printing = Make (struct let encode = encode_record end)")));
        PsiStruct struct = PsiTreeUtil.findChildOfType(module.getBody(), PsiStruct.class);

        assertNotNull(struct);
    }

    public void testModuleFunctorInstanciation2() {
        PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash)\ntype infos");
        Collection<PsiNameIdentifierOwner> expressions = expressions(file);

        assertEquals(2, expressions.size());
    }

}
