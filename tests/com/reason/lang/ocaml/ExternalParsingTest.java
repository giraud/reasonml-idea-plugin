package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiExternal;

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
}
