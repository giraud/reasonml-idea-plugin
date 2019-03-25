package com.reason.lang.core;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.ocaml.OclParserDefinition;

@SuppressWarnings("ConstantConditions")
public class PsiScopedExprTest extends BaseParsingTestCase {
    public PsiScopedExprTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testEmptyScope() {
        PsiLet e = (PsiLet) firstElement(parseCode("let () = x1"));

        assertTrue(ORUtil.findImmediateFirstChildOfClass(e, PsiScopedExpr.class).isEmpty());
    }

    public void testNotEmptyScope() {
        PsiLet e = (PsiLet) firstElement(parseCode("let (a, b) = x"));

        assertFalse(ORUtil.findImmediateFirstChildOfClass(e, PsiScopedExpr.class).isEmpty());
    }

}
