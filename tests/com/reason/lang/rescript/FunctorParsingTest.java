package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctorParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        RPsiFunctor e = firstOfType(parseCode("module Make = (M: Def): S => {}"), RPsiFunctor.class);

        assertEquals("S", e.getReturnType().getText());
        assertEquals("{}", e.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(e.getBody(), RPsiScopedExpr.class));
        assertDoesntContain(extractUpperSymbolTypes(e), myTypes.A_VARIANT_NAME);
    }

    @Test
    public void test_withConstraints() {
        RPsiFunctor e = firstOfType(parseCode("module Make = (M: Input) : (S with type t<'a> = M.t<'a> and type b = M.b) => {}"), RPsiFunctor.class);

        assertEquals("M: Input", first(e.getParameters()).getText());
        assertEquals("S", e.getReturnType().getText());
        assertEquals("{}", e.getBody().getText());

        List<RPsiTypeConstraint> ec = e.getConstraints();
        assertEquals(2, ec.size());
        assertEquals("type t<'a> = M.t<'a>", ec.get(0).getText());
        assertEquals("type b = M.b", ec.get(1).getText());
    }

    @Test
    public void test_signature() {
        Collection<RPsiFunctor> functors = functorExpressions(parseCode(
                "module GlobalBindings = (M: {\n" +
                        "    let relation_classes: list<string>\n" +
                        "    let morphisms: list<string>\n" +
                        "    let arrow: evars => evars\n" +
                        "  },\n" +
                        ") => {\n" +
                        "  open M\n" +
                        "}"));

        assertEquals(1, functors.size());
        RPsiFunctor f = first(functors);
        assertEquals("GlobalBindings", f.getName());
        assertEquals("Dummy.GlobalBindings", f.getQualifiedName());
        Collection<RPsiParameterDeclaration> parameters = f.getParameters();
        assertSize(1, parameters);
        assertEquals("M", first(parameters).getName());
        assertNotNull(f.getBody());
        assertNull(PsiTreeUtil.findChildOfType(f.getBody(), RPsiScopedExpr.class));
    }
}
