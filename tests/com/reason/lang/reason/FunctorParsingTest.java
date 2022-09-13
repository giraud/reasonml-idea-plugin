package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctorParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        PsiNamedElement e = first(expressions(parseCode("module Make = (M: Def): S => {}")));

        PsiFunctor f = (PsiFunctor) e;
        assertEquals("S", f.getReturnType().getText());
        assertEquals("{}", f.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(f.getBody(), PsiScopedExpr.class));
    }

    @Test
    public void test_withConstraints() {
        Collection<PsiNamedElement> expressions = expressions(parseCode("module Make = (M: Input) : (S with type t('a) = M.t('a) and type b = M.b) => {}"));

        assertEquals(1, expressions.size());
        PsiFunctor f = (PsiFunctor) first(expressions);

        assertEquals("M: Input", first(f.getParameters()).getText());
        assertEquals("S", f.getReturnType().getText());

        List<PsiTypeConstraint> constraints = new ArrayList<>(f.getConstraints());
        assertEquals(2, constraints.size());
        assertEquals("type t('a) = M.t('a)", constraints.get(0).getText());
        assertEquals("type b = M.b", constraints.get(1).getText());
        assertEquals("{}", f.getBody().getText());
    }

    @Test
    public void test_signature() {
        Collection<PsiFunctor> functors = functorExpressions(parseCode(
                "module GlobalBindings = (M: {\n" +
                        "    let relation_classes: list(string);\n" +
                        "    let morphisms: list(string);\n" +
                        "    let arrow: evars => evars;\n" +
                        "  },\n" +
                        ") => {\n" +
                        "  open M\n" +
                        "}"));

        assertEquals(1, functors.size());
        PsiFunctor f = first(functors);
        assertEquals("GlobalBindings", f.getName());
        assertEquals("Dummy.GlobalBindings", f.getQualifiedName());
        Collection<PsiParameterDeclaration> parameters = f.getParameters();
        assertSize(1, parameters);
        assertEquals("M", first(parameters).getName());
        assertNotNull(f.getBody());
        assertNull(PsiTreeUtil.findChildOfType(f.getBody(), PsiScopedExpr.class));
    }
}
