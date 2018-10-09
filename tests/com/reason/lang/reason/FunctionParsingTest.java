package com.reason.lang.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiFunctionParameter;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameters;

import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings("ConstantConditions")
public class FunctionParsingTest extends BaseParsingTestCase {
    public FunctionParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testLetFunction() {
        PsiLet e = first(letExpressions(parseCode("let add = (x,y) => x + y;")));

        assertTrue(e.isFunction());
        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertNotNull(function);
        assertEquals(2, first(PsiTreeUtil.findChildrenOfType(function, PsiParameters.class)).getArgumentsCount());
        assertNotNull(function.getBody());
    }

    public void testLetFunctionParenless() {
        PsiLet e = first(letExpressions(parseCode("let add10 = x => x + 10;")));

        assertTrue(e.isFunction());
        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertNotNull(function);
        assertEquals(1, first(PsiTreeUtil.findChildrenOfType(function, PsiParameters.class)).getArgumentsCount());
        assertNotNull(function.getBody());
    }

    public void testAnonFunction() {
        PsiLet e = first(letExpressions(parseCode("let x = Belt.map(items, (. item) => item)")));

        PsiFunction function = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertNotNull(function);
        assertEquals(1, first(PsiTreeUtil.findChildrenOfType(function, PsiParameters.class)).getArgumentsCount());
    }

    public void testBraceFunction() {
        PsiLet e = first(letExpressions(parseCode("let x = (x, y) => { x + y; }")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("(x, y) => { x + y; }", function.getText());
        assertNotNull(function.getBody());
    }

    public void testInnerFunction() {
        PsiLet e = first(letExpressions(parseCode("let _ = error => Belt.Array.mapU(errors, (. error) => error##message);")));

        PsiFunction functionOuter = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("Belt.Array.mapU(errors, (. error) => error##message)", functionOuter.getBody().getText());

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, PsiFunction.class);
        assertEquals("error##message", functionInner.getBody().getText());
    }

    public void testInnerFunctionNoParens() {
        PsiLet e = first(letExpressions(parseCode("let _ = funcall(result => 2);")));

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertEquals("2", functionInner.getBody().getText());
    }

    public void testInnerFunctionBraces() {
        PsiLet e = first(letExpressions(parseCode("let _ = error => { Belt.Array.mapU(errors, (. error) => error##message); };")));

        PsiFunction functionOuter = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("{ Belt.Array.mapU(errors, (. error) => error##message); }", functionOuter.getBody().getText());

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, PsiFunction.class);
        assertEquals("error##message", functionInner.getBody().getText());
    }

    public void testParametersNamedSymbols() {
        PsiLet e = first(letExpressions(parseCode("let make = (~id:string, ~values: option(Js.t('a)), children) => null;")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        Collection<PsiFunctionParameter> parameters = function.getParameterList();
        assertEquals(3, parameters.size());

        Iterator<PsiFunctionParameter> itParams = parameters.iterator();
        assertEquals("id", itParams.next().getName());
        assertEquals("values", itParams.next().getName());
        assertEquals("children", itParams.next().getName());
    }

    public void testParametersLIdent() {
        PsiLet e = first(letExpressions(parseCode("let make = (id, values, children) => null;")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        Collection<PsiFunctionParameter> parameters = function.getParameterList();
        assertEquals(3, parameters.size());

        Iterator<PsiFunctionParameter> itParams = parameters.iterator();
        assertEquals("id", itParams.next().getName());
        assertEquals("values", itParams.next().getName());
        assertEquals("children", itParams.next().getName());
    }

}
