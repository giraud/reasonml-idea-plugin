package com.reason.lang.ocaml;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameters;

import java.util.Collection;

@SuppressWarnings("ConstantConditions")
public class FunctionParsingTest extends BaseParsingTestCase {
    public FunctionParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testLetFunction() {
        PsiLet e = first(letExpressions(parseCode("let add x y = x + y")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();
        assertSize(2, function.getParameters());
        assertNotNull(function.getBody());
    }

    public void testFunctionLetBinding() {
        PsiLet e = first(letExpressions(parseCode("let getAttributes node = let attr = \"r\" in attr")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();
        assertSize(1, function.getParameters());
        assertNotNull(function.getBody());
    }

    public void testFunctionLetBinding2() {
        PsiLet e = first(letExpressions(parseCode("let visit_vo f = Printf.printf \"a\"; Printf.printf \"b\"")));

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

    public void testComplexParams() {
        Collection<PsiNameIdentifierOwner> expressions = expressions(parseCode("let resolve_morphism env ?(fnewt=fun x -> x) args' (b,cstr) = let x = 1"));

        assertSize(1, expressions);
        PsiLet let = (PsiLet) first(expressions);
        assertTrue(let.isFunction());
        assertSize(4, let.getFunction().getParameters());
        assertEquals("let x = 1", let.getFunction().getBody().getText());
    }

}
