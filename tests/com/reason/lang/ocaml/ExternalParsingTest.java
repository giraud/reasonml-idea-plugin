package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLowerSymbol;

public class ExternalParsingTest extends BaseParsingTestCase {
    public ExternalParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testWithString() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass : ReasonReact.reactClass = \"FormattedMessage\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getORSignature().asString(OclLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("FormattedMessage", e.getExternalName());
    }

    public void testWithEmptyString() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getORSignature().asString(OclLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("", e.getExternalName());
    }

    public void testString() {
        PsiExternal e = firstOfType(parseCode("external string : string -> reactElement = \"%identity\"", true), PsiExternal.class);

        assertEquals("string", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getFirstChild().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("string -> reactElement", e.getPsiSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    public void testArray() {
        PsiExternal e = firstOfType(parseCode("external array : reactElement array -> reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("array", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getFirstChild().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("reactElement array -> reactElement", e.getPsiSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }
}
