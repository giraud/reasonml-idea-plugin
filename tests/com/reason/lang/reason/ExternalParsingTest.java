package com.reason.lang.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiSignature;

public class ExternalParsingTest extends BaseParsingTestCase {
    public ExternalParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testSigature() {
        PsiExternal e = externalExpression(parseCode("external props : (string) => string;"), "props");

        PsiSignature signature = PsiTreeUtil.getStubChildOfType(e, PsiSignature.class);
        assertNotNull(signature);
        assertEquals("(string) => string", signature.getText());
        assertTrue(e.isFunction());
    }

    public void testWithString() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"FormattedMessage\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getHMSignature().toString());
        assertFalse(e.isFunction());
        assertEquals("FormattedMessage", e.getExternalName());
    }

    public void testWithEmptyString() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getHMSignature().toString());
        assertFalse(e.isFunction());
        assertEquals("", e.getExternalName());
    }
}
