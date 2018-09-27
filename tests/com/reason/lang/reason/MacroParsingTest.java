package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiMacro;
import com.reason.lang.core.psi.PsiMacroName;
import com.reason.lang.core.psi.PsiRawMacroBody;

public class MacroParsingTest extends BaseParsingTestCase {
    public MacroParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiLet expression = first(letExpressions(parseCode("let _ = [%raw \"xxx\"]")));

        PsiElement macro = expression.getBinding().getFirstChild();
        assertInstanceOf(macro, PsiMacro.class);
        assertEquals("%raw", PsiTreeUtil.findChildOfType(macro, PsiMacroName.class).toString());
        assertEquals("\"xxx\"", PsiTreeUtil.findChildOfType(macro, PsiRawMacroBody.class).getText());
    }
}
