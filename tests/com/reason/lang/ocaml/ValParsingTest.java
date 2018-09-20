package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiVal;

@SuppressWarnings("ConstantConditions")
public class ValParsingTest extends BaseParsingTestCase {
    public ValParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testConstant() {
        PsiVal val = first(valExpressions(parseCode("val x = 1")));
        assertInstanceOf(val.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals("x", val.getName());
    }

    public void testSpecialName() {
        PsiVal val = first(valExpressions(parseCode("val (>>=) : 'a -> 'a t")));
        assertInstanceOf(val.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("(>>=)", val.getName());
    }
}
