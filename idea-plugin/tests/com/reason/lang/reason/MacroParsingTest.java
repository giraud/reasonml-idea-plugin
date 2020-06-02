package com.reason.lang.reason;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

public class MacroParsingTest extends BaseParsingTestCase {
    public MacroParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiLet expression = first(letExpressions(parseCode("let _ = [%raw \"xxx\"]")));

        PsiElement macro = expression.getBinding().getFirstChild();
        assertInstanceOf(macro, PsiMacro.class);

        PsiRawBody rawMacroBody = PsiTreeUtil.findChildOfType(macro, PsiRawBody.class);
        assertEquals("%raw", PsiTreeUtil.findChildOfType(macro, PsiMacroName.class).toString());
        assertEquals("\"xxx\"", rawMacroBody.getText());
        assertEquals(new TextRange(1, 4), rawMacroBody.getMacroTextRange());
    }

    public void testRootRaw() {
        PsiElement e = firstElement(parseCode("%raw \"xxx\";"));

        assertInstanceOf(e, PsiRaw.class);
        PsiRawBody rawBody = PsiTreeUtil.findChildOfType(e, PsiRawBody.class);
        assertEquals("\"xxx\"", rawBody.getText());
    }

    public void testMultiLine() {
        PsiLet expression = first(letExpressions(parseCode("let _ = [%raw {|function (a) {}|}]")));

        PsiElement macro = expression.getBinding().getFirstChild();
        assertInstanceOf(macro, PsiMacro.class);

        PsiRawBody rawMacroBody = PsiTreeUtil.findChildOfType(macro, PsiRawBody.class);
        assertEquals("%raw", PsiTreeUtil.findChildOfType(macro, PsiMacroName.class).toString());
        assertEquals("{|function (a) {}|}", rawMacroBody.getText());
        assertEquals(new TextRange(2, 17), rawMacroBody.getMacroTextRange());
    }
}
