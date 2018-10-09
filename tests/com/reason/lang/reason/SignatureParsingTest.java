package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.psi.PsiLet;

public class SignatureParsingTest extends BaseParsingTestCase {
    public SignatureParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testMandatoryVal() {
        PsiLet let = first(letExpressions(parseCode("let x:int = 1")));

        HMSignature signature = let.getSignature();
        assertTrue(signature.isMandatory(0));
    }

    public void testOptionalFun() {
        PsiLet let = first(letExpressions(parseCode("let x:int => option(string) => string = (a,b) => c")));

        HMSignature signature = let.getSignature();
        assertTrue(signature.isMandatory(0));
        assertFalse(signature.isMandatory(1));
    }

}
