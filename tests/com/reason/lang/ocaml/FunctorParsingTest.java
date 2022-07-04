package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
public class FunctorParsingTest extends OclParsingTestCase {
    public void test_basic() {
        PsiNamedElement e = first(expressions(parseCode("module Make (M:Def) : S = struct end")));

        PsiFunctor f = (PsiFunctor) e;
        assertEquals("struct end", f.getBody().getText());
        assertEquals("S", f.getReturnType().getText());
        PsiParameter p = f.getParameters().iterator().next();
        assertEquals(OclTypes.INSTANCE.C_PARAM_DECLARATION, p.getNode().getElementType());
        List<IElementType> uTypes =
                PsiTreeUtil.findChildrenOfType(e, PsiUpperSymbol.class)
                        .stream()
                        .map(psi -> psi.getNode().getElementType())
                        .collect(Collectors.toList());
        assertDoesntContain(uTypes, myTypes.A_VARIANT_NAME);
    }

    public void test_struct() {
        PsiNamedElement e = first(expressions(parseCode("module Make (struct type t end) : S = struct end")));

        PsiFunctor f = (PsiFunctor) e;
        assertEquals("struct end", f.getBody().getText());
        assertEquals("S", f.getReturnType().getText());
        List<IElementType> uTypes =
                PsiTreeUtil.findChildrenOfType(e, PsiUpperSymbol.class)
                        .stream()
                        .map(psi -> psi.getNode().getElementType())
                        .collect(Collectors.toList());
        assertDoesntContain(uTypes, myTypes.A_VARIANT_NAME);
    }

    public void test_implicit_result() {
        PsiNamedElement e = first(expressions(parseCode("module Make (M:Def) = struct end")));

        PsiFunctor f = (PsiFunctor) e;
        assertEquals("struct end", f.getBody().getText());
    }

    public void test_with_constraints() {
        Collection<PsiNamedElement> expressions = expressions(parseCode(
                "module Make (M: Input) : S with type +'a t = 'a M.t and type b = M.b = struct end"));

        assertEquals(1, expressions.size());
        PsiFunctor f = (PsiFunctor) first(expressions);

        assertEquals("M: Input", first(f.getParameters()).getText());
        assertEquals("S", f.getReturnType().getText());

        List<PsiConstraint> constraints = new ArrayList<>(f.getConstraints());
        assertEquals(2, constraints.size());
        assertEquals("type +'a t = 'a M.t", constraints.get(0).getText());
        assertEquals("type b = M.b", constraints.get(1).getText());
        assertEquals("struct end", f.getBody().getText());
    }

    public void test_signature() {
        Collection<PsiFunctor> functors = functorExpressions(parseCode( //
                "module GlobalBindings (M : sig\n" + //
                        "val relation_classes : string list\n" + //
                        "val morphisms : string list\n" + //
                        "val arrow : evars -> evars * constr\n" + //
                        "end) = struct  open M  end"));

        assertEquals(1, functors.size());
        PsiFunctor functor = first(functors);
        assertEquals("GlobalBindings", functor.getName());
        assertEquals("Dummy.GlobalBindings", functor.getQualifiedName());
        Collection<PsiParameter> parameters = functor.getParameters();
        assertSize(1, parameters);
        assertEquals("M", first(parameters).getName());
        assertNotNull(functor.getBody());
    }
}
