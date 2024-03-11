package com.reason.lang.ocaml;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.psi.impl.RPsiLowerSymbol;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class ExternalParsingTest extends OclParsingTestCase {
    @Test
    public void test_qualifiedName() {
        RPsiExternal e = firstOfType(parseCode("external ee : int = \"\""), RPsiExternal.class);

        assertEquals("Dummy.ee", e.getQualifiedName());
    }

    @Test
    public void test_with_string() {
        RPsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass : ReasonReact.reactClass = \"FormattedMessage\""), RPsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getSignature().asText(OclLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("FormattedMessage", e.getExternalName());
    }

    @Test
    public void test_empty_string() {
        RPsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"\""), RPsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getSignature().asText(OclLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("", e.getExternalName());
    }

    @Test
    public void test_string() {
        RPsiExternalImpl e = (RPsiExternalImpl) firstOfType(parseCode("external string : string -> reactElement = \"%identity\""), RPsiExternal.class);

        assertEquals("string", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("string -> reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    @Test
    public void test_array() {
        RPsiExternalImpl e = (RPsiExternalImpl) firstOfType(parseCode("external array : reactElement array -> reactElement = \"%identity\""), RPsiExternal.class);

        assertEquals("array", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("reactElement array -> reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    @Test
    public void test_raise() {
        RPsiExternalImpl e = (RPsiExternalImpl) firstOfType(parseCode("external raise : exn -> 'a = \"%raise\""), RPsiExternal.class);

        assertEquals("raise", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("exn -> 'a", e.getSignature().getText());
        assertEquals("%raise", e.getExternalName());
    }

    @Test
    public void test_operator1() {
        RPsiExternalImpl e = (RPsiExternalImpl) firstOfType(parseCode("external ( = ) : 'a -> 'a -> bool = \"%equal\""), RPsiExternal.class);

        assertEquals("( = )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%equal", e.getExternalName());
    }

    @Test
    public void test_operator2() {
        RPsiExternalImpl e = firstOfType(parseCode("external (<>) : 'a -> 'a -> bool = \"%notequal\""), RPsiExternalImpl.class);

        assertEquals("(<>)", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%notequal", e.getExternalName());
    }

    @Test
    public void test_operator3() {
        RPsiExternalImpl e = firstOfType(parseCode("external ( < ) : 'a -> 'a -> bool = \"%lessthan\""), RPsiExternalImpl.class);

        assertEquals("( < )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%lessthan", e.getExternalName());
    }

    @Test
    public void test_operator4() {
        RPsiExternalImpl e = firstOfType(parseCode("external ( > ) : 'a -> 'a -> bool = \"%greaterthan\""), RPsiExternalImpl.class);

        assertEquals("( > )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%greaterthan", e.getExternalName());
    }

    @Test
    public void test_operator5() {
        RPsiExternalImpl e = firstOfType(parseCode("external ( <= ) : 'a -> 'a -> bool = \"%lessequal\""), RPsiExternalImpl.class);

        assertEquals("( <= )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%lessequal", e.getExternalName());
    }

    @Test
    public void test_operator6() {
        RPsiExternalImpl e = firstOfType(parseCode("external ( >= ) : 'a -> 'a -> bool = \"%greaterequal\""), RPsiExternalImpl.class);

        assertEquals("( >= )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), RPsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%greaterequal", e.getExternalName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/423
    @Test
    public void test_GH_423() {
        RPsiExternal e = firstOfType(parseCode("external ref : 'a -> 'a ref = \"%makemutable\""), RPsiExternal.class);

        assertEquals("ref", e.getName());
    }
}
