package com.reason.lang.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameters;

@SuppressWarnings("ConstantConditions")
public class FunctionParsingTest extends BaseParsingTestCase {
    public FunctionParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testLetFunction() {
        PsiLet e = first(letExpressions(parseCode("let add x y = x + y")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();
        assertEquals(2, function.getParameterList().size());
        assertNotNull(function.getBody());
    }

    public void testFunctionLetBinding() {
        PsiLet e = first(letExpressions(parseCode("let getAttributes node = let attr = \"r\" in attr")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();
        assertEquals(1, function.getParameterList().size());
        assertNotNull(function.getBody());
    }

    public void testFunctionLetBinding2() {
        PsiLet e = first(letExpressions(parseCode("let visit_vo f = Printf.printf \"a\"; Printf.printf \"b\"", true)));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();
        assertEquals("Printf.printf \"a\"; Printf.printf \"b\"", function.getBody().getText());
    }

    public void testFunctionFun() {
        PsiLet e = first(letExpressions(parseCode("let _ = fun (_, info as ei) -> x")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();
        assertEquals("(_, info as ei)", PsiTreeUtil.findChildOfType(function, PsiParameters.class).getText());
        assertEquals("x", function.getBody().getText());
    }
}
