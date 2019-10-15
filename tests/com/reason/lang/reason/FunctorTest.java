package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiParameter;

import java.util.Collection;

@SuppressWarnings("ConstantConditions")
public class FunctorTest extends BaseParsingTestCase {
    public FunctorTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiNameIdentifierOwner e = first(expressions(parseCode("module Make = (M: Def) : S => {};")));

        assertNotNull(e);
        assertInstanceOf(e, PsiFunctor.class);
    }

    public void testModuleFunctor2() {
        Collection<PsiNameIdentifierOwner> expressions = expressions(parseCode("module Make = (M: Input) : (S with type input = M.t) => {};"));

        assertEquals(1, expressions.size());
        PsiFunctor functor = (PsiFunctor) first(expressions);

        assertEquals("M: Input", first(functor.getParameters()).getText());
        //assertEquals("S with type input = M.t", functor.getReturnType().getText());
        assertEquals("{}", functor.getBinding().getText());
    }

    public void testSignature() {
        Collection<PsiFunctor> functors = functorExpressions(parseCode(
                "module GlobalBindings = (M: {\n" +
                        "           let relation_classes: list(string);\n" +
                        "           let morphisms: list(string);\n" +
                        "           let arrow: evars => evars;\n" +
                        "         },\n" +
                        "       ) => {\n" +
                        "  open M;\n" +
                        "};"));

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
        PsiInnerModule module = first(moduleExpressions(parseCode("module Printing = Make({ let encode = encode_record; });")));

        assertNotNull(module.getBody());
    }

    public void testModuleFunctorInstantiation2() {
        PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash);\ntype infos;");
        Collection<PsiNameIdentifierOwner> expressions = expressions(file);

        assertEquals(2, expressions.size());
    }
}
