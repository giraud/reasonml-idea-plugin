package com.reason.lang.reason;

import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class MacroParsingTest extends RmlParsingTestCase {
    public void test_basic() {
        PsiLet expression = first(letExpressions(parseCode("let _ = [%raw \"xxx\"]")));

        PsiElement macro = expression.getBinding().getFirstChild();
        assertInstanceOf(macro, PsiMacro.class);

        PsiMacroBody rawMacroBody = PsiTreeUtil.findChildOfType(macro, PsiMacroBody.class);
        assertEquals("%raw", PsiTreeUtil.findChildOfType(macro, PsiMacroName.class).getText());
        assertEquals("\"xxx\"", rawMacroBody.getText());
        assertEquals(new TextRange(1, 4), rawMacroBody.getMacroTextRange());
    }

    public void test_rootRaw() {
        PsiMacro e = firstOfType(parseCode("%raw \"xxx\";"), PsiMacro.class);

        assertTrue(e.isRoot());
        assertEquals("\"xxx\"", e.getContent().getText());
    }

    public void test_multiLine() {
        PsiLet expression = first(letExpressions(parseCode("let _ = [%raw {|function (a) {}|}]")));

        PsiMacro macro = (PsiMacro) expression.getBinding().getFirstChild();

        assertEquals("%raw", macro.getName());
        assertEquals("{|function (a) {}|}", macro.getContent().getText());
        assertEquals(new TextRange(2, 17), macro.getContent().getMacroTextRange()); // exclude {| |}
    }
}
