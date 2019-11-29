package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.lang.core.psi.PsiParameter;

public class ResolveParameterElementTest extends LightPlatformCodeInsightFixtureTestCase {

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
