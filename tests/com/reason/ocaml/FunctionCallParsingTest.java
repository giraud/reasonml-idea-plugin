package com.reason.ocaml;

import com.intellij.psi.PsiElement;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLocalOpen;
import com.reason.lang.ocaml.OclParserDefinition;

public class FunctionCallParsingTest extends BaseParsingTestCase {
    public FunctionCallParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testCall() {
        PsiLet e = first(letExpressions(parseCode("let x = Future.force t.(i)")));

        PsiElement call = e.getBinding().getFirstChild();
        assertFalse(call instanceof PsiLocalOpen);
    }

}
