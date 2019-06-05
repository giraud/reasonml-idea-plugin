package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class ResolveLetElementTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testLocalOpenWithParens() {
        myFixture.configureByText("A.re", "module A1 = { let a = 3; };");
        myFixture.configureByText("B.re", "let b = A.(A1.a<caret>);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void testLocalOpenWithParens2() {
        myFixture.configureByText("A.re", "module A1 = { let a = 3; };");
        myFixture.configureByText("B.re", "let a = A.A1.(a<caret>);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

}
