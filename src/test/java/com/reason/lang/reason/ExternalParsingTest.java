package com.reason.lang.reason;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class ExternalParsingTest extends RmlParsingTestCase {
    @Test
    public void test_signature() {
        RPsiExternal e = firstOfType(parseCode("external props : (string) => string;"), RPsiExternal.class);

        RPsiSignature signature = e.getSignature();
        assertEquals("(string) => string", signature.getText());
        assertTrue(e.isFunction());
    }

    @Test
    public void test_named_param() {
        RPsiExternal e = firstOfType(parseCode("external props : (~value:string) => string;"), RPsiExternal.class);

        RPsiSignature signature = e.getSignature();
        assertEquals("(~value:string) => string", signature.getText());
        assertTrue(e.isFunction());
    }

    @Test
    public void test_with_string() {
        RPsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"FormattedMessage\""), RPsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getSignature().asText(getLangProps()));
        assertFalse(e.isFunction());
        assertEquals("FormattedMessage", e.getExternalName());
    }

    @Test
    public void test_with_empty_string() {
        RPsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"\""), RPsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getSignature().asText(getLangProps()));
        assertFalse(e.isFunction());
        assertEquals("", e.getExternalName());
    }

    @Test
    public void test_string() {
        RPsiExternal e = firstOfType(parseCode("external string : string => reactElement = \"%identity\""), RPsiExternal.class);

        assertEquals("string", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiLowerSymbol.class);
        assertEquals("string => reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    @Test
    public void test_array() {
        RPsiExternal e = firstOfType(parseCode("external array : array(reactElement) => reactElement = \"%identity\""), RPsiExternal.class);

        assertEquals("array", e.getName());
        assertEquals("array(reactElement) => reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }
}
