package com.reason.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiIfStatement;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.reason.RmlParserDefinition;

public class IfParsingTest extends BaseParsingTestCase {
    public IfParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasicIfParsing() {
        PsiFileModuleImpl psiFileModule = parseCode("if (x) { (); }");
        PsiIfStatement e = firstOfType(psiFileModule, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getBinaryCondition());
        PsiScopedExpr ifScope = PsiTreeUtil.findChildOfType(e, PsiScopedExpr.class);
        assertNotNull(ifScope);
        // zzz assertEquals("{ (); }", ifScope.getText());
    }
}
