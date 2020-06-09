package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiFunctorCall;
import com.reason.lang.core.psi.PsiFunctorConstraint;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiStruct;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctorTest extends BaseParsingTestCase {
    public FunctorTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testBasic() {
        PsiNameIdentifierOwner e = first(expressions(parseCode("module Make (M:Def) : S = struct end")));

        PsiFunctor f = (PsiFunctor) e;
        assertEquals("struct end", f.getBinding().getText());
        assertEquals("S", f.getReturnType().getText());
    }

    public void testWithConstraints() {
        Collection<PsiNameIdentifierOwner> expressions = expressions(parseCode("module Make (M: Input) : S with type +'a t = 'a M.t and type b = M.b = struct end"));

        assertEquals(1, expressions.size());
        PsiFunctor f = (PsiFunctor) first(expressions);

        assertEquals("M: Input", first(f.getParameters()).getText());
        assertEquals("S", f.getReturnType().getText());

        List<PsiFunctorConstraint> constraints = new ArrayList<>(f.getConstraints());
        assertEquals(2, constraints.size());
        assertEquals("type +'a t = 'a M.t", constraints.get(0).getText());
        assertEquals("type b = M.b", constraints.get(1).getText());
        assertEquals("struct end", f.getBinding().getText());
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

    public void testFunctorInstanciation() {
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(parseCode("module Printing = Make (struct let encode = encode_record end)")));

        assertNull(module.getBody());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(module, PsiFunctorCall.class);
        assertNotNull(call);
        assertEquals("Make (struct let encode = encode_record end)", call.getText());
    }

    public void testFunctorInstanciationChaining() {
        PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash)\ntype infos");
        List<PsiNameIdentifierOwner> expressions = new ArrayList<>(expressions(file));

        assertEquals(2, expressions.size());

        PsiInnerModule module = (PsiInnerModule) expressions.get(0);
        assertNull(module.getBody());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(module, PsiFunctorCall.class);
        assertNotNull(call);
        assertEquals("Hashtbl.Make(KeyHash)", call.getText());
    }

}
