package com.reason.lang.rescript;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class ExternalParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        RPsiExternal e = firstOfType(parseCode("external global : t = \"global\""), RPsiExternal.class);

        RPsiSignature signature = e.getSignature();
        assertEquals("t", signature.getText());
        assertEquals("global", e.getExternalName());
    }

    @Test
    public void test_signature_function() {
        RPsiExternal e = firstOfType(parseCode("external props : string => string = \"\""), RPsiExternal.class);

        RPsiSignature signature = e.getSignature();
        assertEquals("string => string", signature.getText());
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
    public void test_named_param() {
        RPsiExternal e = firstOfType(parseCode("external props : (~value:string) => string = \"\""), RPsiExternal.class);

        RPsiSignature signature = e.getSignature();
        assertEquals("(~value:string) => string", signature.getText());
        assertTrue(e.isFunction());
    }

    @Test
    public void test_string() {
        RPsiExternal e = firstOfType(parseCode("external string: string => reactElement = \"%identity\""), RPsiExternal.class);

        assertEquals("string", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiLowerSymbol.class);
        assertEquals("string => reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    @Test
    public void test_array() {
        RPsiExternal e = firstOfType(parseCode("external array: array<reactElement> => reactElement = \"%identity\""), RPsiExternal.class);

        assertEquals("array", e.getName());
        assertEquals("array<reactElement> => reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    @Test
    public void test_operator2() {
        RPsiExternal e = firstOfType(parseCode("external \\\"<>\": ('a, 'a) => bool = \"%notequal\""), RPsiExternal.class);

        assertEquals("\"<>\"", e.getName());
        assertEquals("('a, 'a) => bool", e.getSignature().getText());
        assertEquals("%notequal", e.getExternalName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/460
    @Test
    public void test_GH_460_keyword_identifiers() {
        RPsiExternal e1 = firstOfType(parseCode("external land: (int,int)=>int = \"%andint\""), RPsiExternal.class);
        assertEquals("land", e1.getName());
        RPsiExternal e2 = firstOfType(parseCode("external lor: (int,int)=>int = \"%orint\""), RPsiExternal.class);
        assertEquals("lor", e2.getName());
        RPsiExternal e3 = firstOfType(parseCode("external lxor: (int,int)=>int = \"%xorint\""), RPsiExternal.class);
        assertEquals("lxor", e3.getName());
        RPsiExternal e4 = firstOfType(parseCode("external lsl: (int,int)=>int = \"%lslint\""), RPsiExternal.class);
        assertEquals("lsl", e4.getName());
        RPsiExternal e5 = firstOfType(parseCode("external lsr: (int,int)=>int = \"%lsrint\""), RPsiExternal.class);
        assertEquals("lsr", e5.getName());
        RPsiExternal e6 = firstOfType(parseCode("external asr: (int,int)=>int = \"%asrint\""), RPsiExternal.class);
        assertEquals("asr", e6.getName());
    }
}
