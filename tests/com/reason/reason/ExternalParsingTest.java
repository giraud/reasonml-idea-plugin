package com.reason.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.reason.RmlParserDefinition;

public class ExternalParsingTest extends BaseParsingTestCase {
    public ExternalParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testSigature() {
        PsiExternal e = externalExpression(parseCode("external props : (string) => string;"), "props");

        PsiSignature signature = PsiTreeUtil.getStubChildOfType(e, PsiSignature.class);
        assertNotNull(signature);
        assertEquals("(string) => string", signature.getText());
    }

}
