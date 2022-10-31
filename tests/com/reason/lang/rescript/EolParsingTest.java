package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class EolParsingTest extends ResParsingTestCase {
    @Test
    public void test_let_chaining() {
        RPsiLetBinding e = firstOfType(parseCode("let _ = if true {\n" +
                "  let _ = 1\n" +
                "  z->fn\n" +
                "}"), RPsiLetBinding.class);
        RPsiIfStatement i = PsiTreeUtil.findChildOfType(e, RPsiIfStatement.class);
        PsiElement it = i.getThenExpression();
        RPsiLet itl = PsiTreeUtil.findChildOfType(it, RPsiLet.class);

        assertEquals("let _ = 1", itl.getText());
    }
}
