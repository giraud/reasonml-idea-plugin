package com.reason.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.reason.RmlParserDefinition;

public class FunctionParsingTest extends BaseParsingTestCase {
    public FunctionParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testLetFunction() {
        PsiLet e = first(parseCode("let add = (x,y) => x + y;", true).getLetExpressions());

        assertTrue(e.isFunction());
        assertEquals(2, first(PsiTreeUtil.findChildrenOfType(e, PsiParameters.class)).getArgumentsCount());
    }

    public void testLetFunctionParenless() {
        PsiLet e = first(parseCode("let add10 = x => x + 10;").getLetExpressions());

        assertTrue(e.isFunction());
        assertEquals(1, first(PsiTreeUtil.findChildrenOfType(e, PsiParameters.class)).getArgumentsCount());
    }

    public void testAnonFunction() {
        PsiLet e = first(parseCode("let x = Belt.map(items, (. item) => item)", true).getLetExpressions());

        assertEquals(1, first(PsiTreeUtil.findChildrenOfType(e, PsiParameters.class)).getArgumentsCount());
    }

}
