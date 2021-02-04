package com.reason.lang.napkin;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class ExternalParsingTest extends NsParsingTestCase {

    public void test_signature() {
        PsiExternal e = firstOfType(parseCode("external e : string => string"), PsiExternal.class);

        assertEquals("e", e.getName());
        assertTrue(e.isFunction());
        assertEquals("string => string", e.getSignature().getText());
    }

    public void test_with_string() {
        PsiExternal e = firstOfType(parseCode("external e: ReasonReact.reactClass = \"FormattedMessage\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getSignature().getText());
        assertFalse(e.isFunction());
        assertEquals("FormattedMessage", e.getExternalName());
    }

    public void test_empty_string() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getSignature().getText());
        assertFalse(e.isFunction());
        assertEquals("", e.getExternalName());
    }

    public void test_array() {
        PsiExternal e = firstOfType(parseCode("external myArray : array<reactElement> => reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("myArray", e.getName());
        assertEquals("array<reactElement> => reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    public void test_string_identifier() {
        PsiExternal e = firstOfType(parseCode("external string : string => reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("string", e.getName());
        assertInstanceOf(((PsiExternalImpl) e).getNameIdentifier(), PsiLowerIdentifier.class);
        assertEquals("string => reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }
}
