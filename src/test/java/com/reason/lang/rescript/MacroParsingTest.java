package com.reason.lang.rescript;

import com.intellij.openapi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class MacroParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        RPsiLet expression = firstOfType(parseCode("let _ = %raw(\"xxx\")"), RPsiLet.class);

        RPsiMacro macro = (RPsiMacro) expression.getBinding().getFirstChild();

        RPsiMacroBody rawMacroBody = macro.getContent();
        assertEquals("%raw", macro.getName());
        assertEquals("\"xxx\"", rawMacroBody.getText());
        assertEquals(new TextRange(1, 4), rawMacroBody.getMacroTextRange());
    }

    @Test
    public void test_rootRaw() {
        RPsiMacro e = firstOfType(parseCode("%%raw(\"xxx\")"), RPsiMacro.class);

        assertTrue(e.isRoot());
        assertEquals("%%raw", e.getName());
        assertEquals("\"xxx\"", e.getContent().getText());
    }

    @Test
    public void test_multiLine() {
        RPsiLet expression = firstOfType(parseCode("let _ = %raw(\"function (a) {}\")"), RPsiLet.class);

        RPsiMacro macro = (RPsiMacro) expression.getBinding().getFirstChild();

        RPsiMacroBody body = macro.getContent();
        assertEquals("%raw", macro.getName());
        assertEquals("\"function (a) {}\"", body.getText());
        assertEquals(new TextRange(1, 16), body.getMacroTextRange());  // exclude `
    }
}
