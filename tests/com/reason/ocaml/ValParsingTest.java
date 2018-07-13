package com.reason.ocaml;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.ocaml.OclParserDefinition;

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
        PsiVal val = first(valExpressions(parseCode("val (>>=) : 'a -> 'a t", true)));
        assertInstanceOf(val.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("(>>=)", val.getName());
    }
}
