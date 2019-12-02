package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiParameter;

public class ResolveParameterElementTest extends ORBasePlatformTestCase {

    public void testRmlParenLess() {
        myFixture.configureByText("A.re", "let add10 = x => x<caret> + 10;");

        PsiElement e = myFixture.getElementAtCaret();
        assertInstanceOf(e.getParent(), PsiParameter.class);
    }

    public void testOclParenLess() {
        myFixture.configureByText("A.ml", "let add10 x = x<caret> + 10");

        PsiElement e = myFixture.getElementAtCaret();
        assertInstanceOf(e.getParent(), PsiParameter.class);
    }

}
