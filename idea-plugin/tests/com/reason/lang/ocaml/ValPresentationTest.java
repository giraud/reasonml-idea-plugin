package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiVal;

@SuppressWarnings("ConstantConditions")
public class ValPresentationTest extends BaseParsingTestCase {
    public ValPresentationTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testNoSig() {
        PsiVal e = first(valExpressions(parseCode("val x = 1")));

        assertEquals("x", e.getPresentation().getPresentableText());
    }

    public void testSig() {
        PsiVal e = first(valExpressions(parseCode("val x : 'a -> 'a t")));

        assertEquals("x: 'a -> 'a t", e.getPresentation().getPresentableText());
    }
}
