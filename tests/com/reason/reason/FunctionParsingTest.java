package com.reason.reason;

import com.intellij.psi.PsiElement;
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
        assertNotNull(PsiTreeUtil.findChildrenOfType(e.getBinding(), PsiFunction.class));
        assertEquals(2, first(PsiTreeUtil.findChildrenOfType(e, PsiParameters.class)).getArgumentsCount());
    }

    public void testLetFunctionParenless() {
        PsiLet e = first(parseCode("let add10 = x => x + 10;").getLetExpressions());

        assertTrue(e.isFunction());
        assertNotNull(PsiTreeUtil.findChildrenOfType(e.getBinding(), PsiFunction.class));
        assertEquals(1, first(PsiTreeUtil.findChildrenOfType(e, PsiParameters.class)).getArgumentsCount());
    }

    public void testAnonFunction() {
        PsiLet e = first(parseCode("let x = Belt.map(items, (. item) => item)").getLetExpressions());

        assertNotNull(PsiTreeUtil.findChildrenOfType(e.getBinding(), PsiFunction.class));
        assertEquals(1, first(PsiTreeUtil.findChildrenOfType(e, PsiParameters.class)).getArgumentsCount());
    }

    public void testBraceFunction() {
        PsiLet e = first(parseCode("let x = (x, y) => { x + y; }").getLetExpressions());

        PsiElement function = e.getBinding().getFirstChild();
        assertInstanceOf(function, PsiFunction.class);
        assertEquals("(x, y) => { x + y; }", function.getText());
    }

}
