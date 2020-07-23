package com.reason.lang.ocaml;

import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiScopedExpr;

@SuppressWarnings("ConstantConditions")
public class ExternalParsingTest extends OclParsingTestCase {
    public void test_qualifiedName() {
        PsiExternal e = firstOfType(parseCode("external ee : int = \"\""), PsiExternal.class);

        assertEquals("Dummy.ee", e.getQualifiedName());
    }

    public void test_withString() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass : ReasonReact.reactClass = \"FormattedMessage\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getORSignature().asString(OclLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("FormattedMessage", e.getExternalName());
    }

    public void test_withEmptyString() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getORSignature().asString(OclLanguage.INSTANCE));
        assertFalse(e.isFunction());
        assertEquals("", e.getExternalName());
    }

    public void test_string() {
        PsiExternal e = firstOfType(parseCode("external string : string -> reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("string", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getFirstChild().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("string -> reactElement", e.getPsiSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    public void test_array() {
        PsiExternal e = firstOfType(parseCode("external array : reactElement array -> reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("array", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getFirstChild().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("reactElement array -> reactElement", e.getPsiSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    public void test_raise() {
        PsiExternal e = firstOfType(parseCode("external raise : exn -> 'a = \"%raise\""), PsiExternal.class);

        assertEquals("raise", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals(e.getNameIdentifier().getFirstChild().getNode().getElementType(), OclTypes.INSTANCE.LIDENT);
        assertEquals("exn -> 'a", e.getPsiSignature().getText());
        assertEquals("%raise", e.getExternalName());
    }

    public void test_operator1() {
        PsiExternal e = firstOfType(parseCode("external ( = ) : 'a -> 'a -> bool = \"%equal\""), PsiExternal.class);

        assertEquals("( = )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getPsiSignature().getText());
        assertEquals("%equal", e.getExternalName());
    }

    public void test_operator2() {
        PsiExternal e = firstOfType(parseCode("external ( <> ) : 'a -> 'a -> bool = \"%notequal\""), PsiExternal.class);

        assertEquals("( <> )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getPsiSignature().getText());
        assertEquals("%notequal", e.getExternalName());
    }

    public void test_operator3() {
        PsiExternal e = firstOfType(parseCode("external ( < ) : 'a -> 'a -> bool = \"%lessthan\""), PsiExternal.class);

        assertEquals("( < )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getPsiSignature().getText());
        assertEquals("%lessthan", e.getExternalName());
    }

    public void test_operator4() {
        PsiExternal e = firstOfType(parseCode("external ( > ) : 'a -> 'a -> bool = \"%greaterthan\""), PsiExternal.class);

        assertEquals("( > )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getPsiSignature().getText());
        assertEquals("%greaterthan", e.getExternalName());
    }

    public void test_operator5() {
        PsiExternal e = firstOfType(parseCode("external ( <= ) : 'a -> 'a -> bool = \"%lessequal\""), PsiExternal.class);

        assertEquals("( <= )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getPsiSignature().getText());
        assertEquals("%lessequal", e.getExternalName());
    }

    public void test_operator6() {
        PsiExternal e = firstOfType(parseCode("external ( >= ) : 'a -> 'a -> bool = \"%greaterequal\""), PsiExternal.class);

        assertEquals("( >= )", e.getName());
        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("'a -> 'a -> bool", e.getPsiSignature().getText());
        assertEquals("%greaterequal", e.getExternalName());
    }
}
