package com.reason.lang.ocaml;

import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.impl.PsiExternalImpl;
import com.reason.lang.core.psi.impl.PsiLowerSymbol;
import com.reason.lang.core.psi.impl.PsiScopedExpr;

@SuppressWarnings("ConstantConditions")
public class ExternalParsingTest extends OclParsingTestCase {
    public void test_qualifiedName() {
        PsiExternal e = firstOfType(parseCode("external ee : int = \"\""), PsiExternal.class);

        assertEquals("Dummy.ee", e.getQualifiedName());
    }

    public void test_with_string() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass : ReasonReact.reactClass = \"FormattedMessage\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getSignature().asText(OclLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("FormattedMessage", e.getExternalName());
    }

    public void test_empty_string() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getSignature().asText(OclLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("", e.getExternalName());
    }

    public void test_string() {
        PsiExternalImpl e = (PsiExternalImpl) firstOfType(parseCode("external string : string -> reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("string", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("string -> reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    public void test_array() {
        PsiExternalImpl e = (PsiExternalImpl) firstOfType(parseCode("external array : reactElement array -> reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("array", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("reactElement array -> reactElement", e.getSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    public void test_raise() {
        PsiExternalImpl e = (PsiExternalImpl) firstOfType(parseCode("external raise : exn -> 'a = \"%raise\""), PsiExternal.class);

        assertEquals("raise", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("exn -> 'a", e.getSignature().getText());
        assertEquals("%raise", e.getExternalName());
    }

    public void test_operator1() {
        PsiExternalImpl e = (PsiExternalImpl) firstOfType(parseCode("external ( = ) : 'a -> 'a -> bool = \"%equal\""), PsiExternal.class);

        assertEquals("( = )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%equal", e.getExternalName());
    }

    public void test_operator2() {
        PsiExternalImpl e = firstOfType(parseCode("external ( <> ) : 'a -> 'a -> bool = \"%notequal\""), PsiExternalImpl.class);

        assertEquals("( <> )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%notequal", e.getExternalName());
    }

    public void test_operator3() {
        PsiExternalImpl e = firstOfType(parseCode("external ( < ) : 'a -> 'a -> bool = \"%lessthan\""), PsiExternalImpl.class);

        assertEquals("( < )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%lessthan", e.getExternalName());
    }

    public void test_operator4() {
        PsiExternalImpl e = firstOfType(parseCode("external ( > ) : 'a -> 'a -> bool = \"%greaterthan\""), PsiExternalImpl.class);

        assertEquals("( > )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%greaterthan", e.getExternalName());
    }

    public void test_operator5() {
        PsiExternalImpl e = firstOfType(parseCode("external ( <= ) : 'a -> 'a -> bool = \"%lessequal\""), PsiExternalImpl.class);

        assertEquals("( <= )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%lessequal", e.getExternalName());
    }

    public void test_operator6() {
        PsiExternalImpl e = firstOfType(parseCode("external ( >= ) : 'a -> 'a -> bool = \"%greaterequal\""), PsiExternalImpl.class);

        assertEquals("( >= )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getSignature().getText());
        assertEquals("%greaterequal", e.getExternalName());
    }
}
