package com.reason.lang.reason;

import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctorParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        RPsiFunctor e = firstOfType(parseCode("module Make = (M:T0, N:T1): S => {};"), RPsiFunctor.class);

        assertNoParserError(e);
        assertEquals("S", e.getReturnType().getText());
        assertEquals("{}", e.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(e.getBody(), RPsiScopedExpr.class));
        List<RPsiParameterDeclaration> eps = e.getParameters();
        assertSize(2, eps);
        assertEquals("M:T0", eps.get(0).getText());
        assertEquals("T0", eps.get(0).getSignature().getText());
        assertEquals("N:T1", eps.get(1).getText());
        assertEquals("T1", eps.get(1).getSignature().getText());
        assertEquals(myTypes.C_PARAM_DECLARATION, eps.get(0).getNode().getElementType());
        assertDoesntContain(extractUpperSymbolTypes(e), myTypes.A_VARIANT_NAME);
    }

    @Test
    public void test_struct() {
        RPsiFunctor e = firstOfType(parseCode("module Make = ({ type t; }): S => {};"), RPsiFunctor.class);

        assertNoParserError(e);
        assertEquals("{}", e.getBody().getText());
        assertEquals("S", e.getReturnType().getText());
        List<IElementType> uTypes = extractUpperSymbolTypes(e);
        assertDoesntContain(uTypes, myTypes.A_VARIANT_NAME);
    }

    @Test
    public void test_implicit_result() {
        RPsiFunctor e = firstOfType(parseCode("module Make = (M:Def) => {};"), RPsiFunctor.class);

        assertNoParserError(e);
        assertEquals("{}", e.getBody().getText());
    }

    @Test
    public void test_with_constraints() {
        RPsiFunctor e = firstOfType(parseCode("module Make = (M: Input) : (S with type t('a) = M.t('a) and type b = M.b) => {}"), RPsiFunctor.class);

        assertNoParserError(e);
        assertEquals("M: Input", first(e.getParameters()).getText());
        assertEquals("S", e.getReturnType().getText());
        assertEquals("{}", e.getBody().getText());

        List<RPsiTypeConstraint> ec = e.getConstraints();
        assertEquals(2, ec.size());
        assertEquals("type t('a) = M.t('a)", ec.get(0).getText());
        assertEquals("type b = M.b", ec.get(1).getText());
    }

    @Test
    public void test_signature() {
        Collection<RPsiFunctor> functors = functorExpressions(parseCode("""
                module GlobalBindings = (M: {
                    let relation_classes: list(string);
                    let morphisms: list(string);
                    let arrow: evars => evars;
                  },
                ) => {
                  open M
                }
                """));

        assertEquals(1, functors.size());
        RPsiFunctor f = first(functors);
        assertNoParserError(f);
        assertEquals("GlobalBindings", f.getName());
        assertEquals("Dummy.GlobalBindings", f.getQualifiedName());
        Collection<RPsiParameterDeclaration> parameters = f.getParameters();
        assertSize(1, parameters);
        assertEquals("M", first(parameters).getName());
        assertNotNull(f.getBody());
        assertNull(PsiTreeUtil.findChildOfType(f.getBody(), RPsiScopedExpr.class));
    }
}
