package com.reason.lang.ocaml;

import java.util.*;
import java.util.stream.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiConstraint;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiFunctorCall;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiUpperSymbol;

@SuppressWarnings("ConstantConditions")
public class FunctorParsingTest extends OclParsingTestCase {
    public void test_basic() {
        PsiNameIdentifierOwner e = first(expressions(parseCode("module Make (M:Def) : S = struct end")));

        PsiFunctor f = (PsiFunctor) e;
        assertEquals("struct end", f.getBinding().getText());
        assertEquals("S", f.getReturnType().getText());
        List<IElementType> uTypes = PsiTreeUtil.findChildrenOfType(e, PsiUpperSymbol.class).stream().map(psi -> psi.getFirstChild().getNode().getElementType())
                .collect(Collectors.toList());
        assertDoesntContain(uTypes, m_types.VARIANT_NAME);
    }

    public void test_implicitResult() {
        PsiNameIdentifierOwner e = first(expressions(parseCode("module Make (M:Def) = struct end")));

        PsiFunctor f = (PsiFunctor) e;
        assertEquals("struct end", f.getBinding().getText());
    }

    public void test_withConstraints() {
        Collection<PsiNameIdentifierOwner> expressions = expressions(
                parseCode("module Make (M: Input) : S with type +'a t = 'a M.t and type b = M.b = struct end"));

        assertEquals(1, expressions.size());
        PsiFunctor f = (PsiFunctor) first(expressions);

        assertEquals("M: Input", first(f.getParameters()).getText());
        assertEquals("S", f.getReturnType().getText());

        List<PsiConstraint> constraints = new ArrayList<>(f.getConstraints());
        assertEquals(2, constraints.size());
        assertEquals("type +'a t = 'a M.t", constraints.get(0).getText());
        assertEquals("type b = M.b", constraints.get(1).getText());
        assertEquals("struct end", f.getBinding().getText());
    }

    public void test_signature() {
        Collection<PsiFunctor> functors = functorExpressions(parseCode(//
                                                                       "module GlobalBindings (M : sig\n" +//
                                                                               "val relation_classes : string list\n" +//
                                                                               "val morphisms : string list\n" +//
                                                                               "val arrow : evars -> evars * constr\n" +//
                                                                               "end) = struct  open M  end"));

        assertEquals(1, functors.size());
        PsiFunctor functor = first(functors);
        assertEquals("GlobalBindings", functor.getName());
        assertEquals("Dummy.GlobalBindings", functor.getQualifiedName());
        Collection<PsiParameter> parameters = functor.getParameters();
        assertSize(1, parameters);
        assertEquals("M", first(parameters).getName());
        assertNotNull(functor.getBinding());
    }

    public void test_functorInstanciation() {
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(parseCode("module Printing = Make (struct let encode = encode_record end)")));

        assertNull(module.getBody());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(module, PsiFunctorCall.class);
        assertNotNull(call);
        assertEquals("Make (struct let encode = encode_record end)", call.getText());
    }

    public void test_functorInstanciationChaining() {
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
