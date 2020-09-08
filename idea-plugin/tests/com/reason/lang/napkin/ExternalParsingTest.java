package com.reason.lang.napkin;

import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.psi.impl.PsiExternalImpl;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;

@SuppressWarnings("ConstantConditions")
public class ExternalParsingTest extends NsParsingTestCase {

    public void test_signature() {
        PsiExternal e = firstOfType(parseCode("external e : string => string"), PsiExternal.class);

        assertEquals("e", e.getName());
        assertTrue(e.isFunction());
        PsiSignature signature = e.getPsiSignature();
        assertEquals("string => string", signature.getText());
    }

    public void test_withString() {
        PsiExternal e = firstOfType(parseCode("external e: ReasonReact.reactClass = \"FormattedMessage\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getORSignature().asString(myLanguage));
        assertFalse(e.isFunction());
        assertEquals("FormattedMessage", e.getExternalName());
    }

    public void test_emptyString() {
        PsiExternal e = firstOfType(parseCode("external reactIntlJsReactClass: ReasonReact.reactClass = \"\""), PsiExternal.class);

        assertEquals("ReasonReact.reactClass", e.getORSignature().asString(myLanguage));
        assertFalse(e.isFunction());
        assertEquals("", e.getExternalName());
    }

    public void test_array() {
        PsiExternal e = firstOfType(parseCode("external myArray : array<reactElement> => reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("myArray", e.getName());
        assertEquals("array<reactElement> => reactElement", e.getPsiSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }

    public void test_stringIdentifier() {
        PsiExternal e = firstOfType(parseCode("external string : string => reactElement = \"%identity\""), PsiExternal.class);

        assertEquals("string", e.getName());
        assertInstanceOf(((PsiExternalImpl) e).getNameIdentifier(), PsiLowerIdentifier.class);
        assertEquals("string => reactElement", e.getPsiSignature().getText());
        assertEquals("%identity", e.getExternalName());
    }
}
