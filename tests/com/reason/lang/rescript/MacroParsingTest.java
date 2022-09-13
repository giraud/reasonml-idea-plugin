package com.reason.lang.rescript;

import com.intellij.openapi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class MacroParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        PsiLet expression = first(letExpressions(parseCode("let _ = %raw(\"xxx\")")));

        PsiMacro macro = (PsiMacro) expression.getBinding().getFirstChild();

        PsiMacroBody rawMacroBody = macro.getContent();
        assertEquals("%raw", macro.getName());
        assertEquals("\"xxx\"", rawMacroBody.getText());
        assertEquals(new TextRange(1, 4), rawMacroBody.getMacroTextRange());
    }

    @Test
    public void test_rootRaw() {
        PsiMacro e = firstOfType(parseCode("%%raw(\"xxx\")"), PsiMacro.class);

        assertTrue(e.isRoot());
        assertEquals("%%raw", e.getName());
        assertEquals("\"xxx\"", e.getContent().getText());
    }

    @Test
    public void test_multiLine() {
        PsiLet expression = first(letExpressions(parseCode("let _ = %raw(\"function (a) {}\")")));

        PsiMacro macro = (PsiMacro) expression.getBinding().getFirstChild();

        PsiMacroBody body = macro.getContent();
        assertEquals("%raw", macro.getName());
        assertEquals("\"function (a) {}\"", body.getText());
        assertEquals(new TextRange(1, 16), body.getMacroTextRange());  // exclude `
    }
}
