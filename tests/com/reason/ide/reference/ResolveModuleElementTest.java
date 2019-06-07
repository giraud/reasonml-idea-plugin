package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class ResolveModuleElementTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testWithAlias() {
        myFixture.configureByText("A.re", "module A1 = {};");
        myFixture.configureByText("B.re", "module X = A; X.A1<caret>);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

}
