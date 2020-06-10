package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiIfStatement;
import com.reason.lang.core.psi.PsiScopedExpr;

import java.util.ArrayList;
import java.util.List;

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

    public void testBasicIfElseNoBraceParsing() {
        PsiFile psiFile = parseCode("let test = x => if (x) 1 else 2;");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getBinaryCondition());
        List<PsiScopedExpr> scopes = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiScopedExpr.class));
        //zzz
        //assertEquals(2, scopes.size());
        //assertEquals("1", scopes.get(0).getText());
        //assertEquals("2", scopes.get(1).getText());
    }

    public void testBasicIfElseParsing() {
        PsiFile psiFile = parseCode("let test = x => if (x) { 1; } else { 2; };");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getBinaryCondition());
        List<PsiScopedExpr> scopes = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiScopedExpr.class));
        assertEquals(2, scopes.size());
        assertEquals("{ 1; }", scopes.get(0).getText());
        assertEquals("{ 2; }", scopes.get(1).getText());
    }
}
