package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiIfStatement;
import com.reason.lang.core.psi.PsiScopedExpr;

public class IfParsingTest extends BaseParsingTestCase {
    public IfParsingTest() {
        super("", "re", new OclParserDefinition());
    }

    public void testBasicIfParsing() {
        PsiFile psiFile = parseCode("let _ = if x then ()");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getBinaryCondition());
        PsiScopedExpr ifScope = PsiTreeUtil.findChildOfType(e, PsiScopedExpr.class);
        assertNotNull(ifScope);
        assertEquals("()", ifScope.getText());
    }
}
