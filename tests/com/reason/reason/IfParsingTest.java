package com.reason.reason;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiIfStatement;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.reason.RmlParserDefinition;

public class IfParsingTest extends BaseParsingTestCase {
    public IfParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasicIfParsing() {
        PsiFile psiFile = parseCode("if (x) { (); }");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getBinaryCondition());
        PsiScopedExpr ifScope = PsiTreeUtil.findChildOfType(e, PsiScopedExpr.class);
        assertNotNull(ifScope);
        // zzz assertEquals("{ (); }", ifScope.getText());
    }
}
