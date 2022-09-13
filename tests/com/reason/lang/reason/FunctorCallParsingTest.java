package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctorCallParsingTest extends RmlParsingTestCase {
    @Test
    public void test_instantiation() {
        PsiInnerModule e = (PsiInnerModule) first(moduleExpressions(parseCode("module Printing = Make({ let encode = encode_record; });")));

        assertTrue(e.isFunctorCall());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class);
        assertEquals("Make({ let encode = encode_record; })", call.getText());
        assertEquals(myTypes.A_MODULE_NAME, call.getNavigationElement().getNode().getElementType());
        assertSize(1, call.getParameters());
        assertEquals("{ let encode = encode_record; }", call.getParameters().iterator().next().getText());
        PsiLet let = PsiTreeUtil.findChildOfType(e, PsiLet.class);
        assertEquals("Dummy.Printing.Make[0].encode", let.getQualifiedName());
    }

    @Test
    public void test_with_path() {
        PsiInnerModule e = (PsiInnerModule) first(moduleExpressions(parseCode("module X = A.B.Make({})")));

        assertTrue(e.isFunctorCall());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class);
        assertEquals("Make({})", call.getText());
        assertEquals("Make", call.getName());
    }

    @Test
    public void test_chaining() {
        PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash);\ntype infos;");
        List<PsiNamedElement> es = new ArrayList<>(expressions(file));

        assertEquals(2, es.size());

        PsiInnerModule module = (PsiInnerModule) es.get(0);
        assertTrue(module.isFunctorCall());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(module, PsiFunctorCall.class);
        assertEquals("Make(KeyHash)", call.getText());
        assertEquals("Make", call.getName());
        assertNull(PsiTreeUtil.findChildOfType(module, PsiParameterDeclaration.class));
    }
}
