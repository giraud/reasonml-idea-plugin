package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctorParsingTest extends OclParsingTestCase {
    @Test
    public void test_basic() {
        RPsiFunctor e = firstOfType(parseCode("module Make (M:T0) (N:T1) : S = struct end"), RPsiFunctor.class);

        assertNoParserError(e);
        assertEquals("S", e.getReturnType().getText());
        assertEquals("struct end", e.getBody().getText());
        List<RPsiParameterDeclaration> eps = e.getParameters();
        assertSize(2, eps);
        assertEquals("(M:T0)", eps.get(0).getText());   // parens outside ?
        assertEquals("T0", eps.get(0).getSignature().getText());
        assertEquals("(N:T1)", eps.get(1).getText());
        assertEquals("T1", eps.get(1).getSignature().getText());
        assertEquals(myTypes.C_PARAM_DECLARATION, eps.get(0).getNode().getElementType());
        assertDoesntContain(extractUpperSymbolTypes(e), myTypes.A_VARIANT_NAME);
    }

    @Test
    public void test_struct() {
        RPsiFunctor e = firstOfType(parseCode("module Make (_:sig type t end) : S = struct end"), RPsiFunctor.class);

        assertEquals("struct end", e.getBody().getText());
        assertEquals("S", e.getReturnType().getText());
        List<IElementType> uTypes = extractUpperSymbolTypes(e);
        assertDoesntContain(uTypes, myTypes.A_VARIANT_NAME);
    }

    @Test
    public void test_implicit_result() {
        RPsiFunctor e = firstOfType(parseCode("module Make (M:Def) = struct end"), RPsiFunctor.class);

        assertNoParserError(e);
        assertEquals("struct end", e.getBody().getText());
    }

    @Test
    public void test_with_constraints() {
        Collection<PsiNamedElement> expressions = expressions(parseCode("module Make (M: Input) : S with type +'a t = 'a M.t and type b = M.b = struct end"));

        assertEquals(1, expressions.size());
        RPsiFunctor f = (RPsiFunctor) first(expressions);

        assertNoParserError(f);
        assertEquals("(M: Input)", first(f.getParameters()).getText());
        assertEquals("S", f.getReturnType().getText());

        List<RPsiTypeConstraint> constraints = new ArrayList<>(f.getConstraints());
        assertEquals(2, constraints.size());
        assertEquals("type +'a t = 'a M.t", constraints.get(0).getText());
        assertEquals("type b = M.b", constraints.get(1).getText());
        assertEquals("struct end", f.getBody().getText());
    }

    @Test
    public void test_with_constraints_parens() {
        RPsiFunctor e = firstOfType(parseCode("module Make(M: SeqType) : (S with type t = M.t) = struct end"), RPsiFunctor.class);
        List<RPsiTypeConstraint> ec = e.getConstraints();

        assertNoParserError(e);
        assertEquals("(M: SeqType)", e.getParameters().get(0).getText());
        assertEquals("SeqType", e.getParameters().get(0).getSignature().getText());
        assertEquals("S", e.getReturnType().getText());

        assertEquals(1, ec.size());
        assertEquals("type t = M.t", ec.get(0).getText());
        assertEquals("struct end", e.getBody().getText());
    }

    @Test
    public void test_signature() {
        Collection<RPsiFunctor> functors = functorExpressions(parseCode(
                """
                module GlobalBindings (M : sig
                  val relation_classes : string list
                  val morphisms : string list
                  val arrow : evars -> evars * constr
                end) = struct
                  open M
                end
                """));

        assertEquals(1, functors.size());
        RPsiFunctor functor = first(functors);
        assertNoParserError(functor);
        assertEquals("GlobalBindings", functor.getName());
        assertEquals("Dummy.GlobalBindings", functor.getQualifiedName());
        Collection<RPsiParameterDeclaration> parameters = functor.getParameters();
        assertSize(1, parameters);
        assertEquals("M", first(parameters).getName());
        assertNotNull(functor.getBody());
    }

    @Test
    public void test_functor_inside_module() {
        RPsiModule e = firstOfType(parseCode("""
                module Core = struct
                  module Make() = struct
                    type t
                  end
                end
                """), RPsiModule.class);

        assertEquals("Core", e.getModuleName());
        assertFalse(e instanceof RPsiFunctor);
        RPsiFunctor ef = firstOfType(e.getBody(), RPsiFunctor.class);
        assertEquals("Make", ef.getModuleName());
        assertTrue(ef instanceof RPsiFunctor);
    }}
