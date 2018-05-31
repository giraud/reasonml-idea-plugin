package com.reason.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.reason.RmlParserDefinition;

public class FunctionParsingTest extends BaseParsingTestCase {
    public FunctionParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testLetFunction() {
        PsiLet e = first(parseCode("let add = (x,y) => x + y;").getLetExpressions());

        assertTrue(e.isFunction());
        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertNotNull(function);
        assertEquals(2, first(PsiTreeUtil.findChildrenOfType(function, PsiParameters.class)).getArgumentsCount());
        assertNotNull(function.getBody());
    }

    public void testLetFunctionParenless() {
        PsiLet e = first(parseCode("let add10 = x => x + 10;").getLetExpressions());

        assertTrue(e.isFunction());
        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertNotNull(function);
        assertEquals(1, first(PsiTreeUtil.findChildrenOfType(function, PsiParameters.class)).getArgumentsCount());
        assertNotNull(function.getBody());
    }

    public void testAnonFunction() {
        PsiLet e = first(parseCode("let x = Belt.map(items, (. item) => item)").getLetExpressions());

        PsiFunction function = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertNotNull(function);
        assertEquals(1, first(PsiTreeUtil.findChildrenOfType(function, PsiParameters.class)).getArgumentsCount());
    }

    public void testBraceFunction() {
        PsiLet e = first(parseCode("let x = (x, y) => { x + y; }").getLetExpressions());

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("(x, y) => { x + y; }", function.getText());
        assertNotNull(function.getBody());
    }

    public void testInnerFunction() {
        PsiLet e = first(parseCode("let _ = error => Belt.Array.mapU(errors, (. error) => error##message);").getLetExpressions());

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertNotNull(function.getBody());
    }

    public void testInnerFunctionBraces() {
        PsiLet e = first(parseCode("let _ = error => { Belt.Array.mapU(errors, (. error) => error##message); };").getLetExpressions());

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertNotNull(function.getBody());
    }

}
