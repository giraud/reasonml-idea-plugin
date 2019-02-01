package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiSignature;

public class ExternalParsingTest extends BaseParsingTestCase {
    public ExternalParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testSigature() {
        PsiExternal e = externalExpression(parseCode("external props : (string) => string;"), "props");

        PsiSignature signature = e.getPsiSignature();
        assertEquals("(string) => string", signature.getText());
        assertTrue(e.isFunction());
    }

    public void testWithString() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"FormattedMessage\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getORSignature().asString(RmlLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("FormattedMessage", e.getExternalName());
    }

    public void testWithEmptyString() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getORSignature().asString(RmlLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("", e.getExternalName());
    }

    public void testString() {
        PsiExternal e = firstOfType(parseCode("external string : string => reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("string", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals("string => reactElement", e.getPsiSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    public void testArray() {
        PsiExternal e = firstOfType(parseCode("external array : array(reactElement) => reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("array", e.getName());
        assertEquals("array(reactElement) => reactElement", e.getPsiSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

}
