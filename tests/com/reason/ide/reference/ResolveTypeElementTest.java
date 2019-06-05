package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class ResolveTypeElementTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testSameNameWithPath() {
        myFixture.configureByText("A.re", "type t;");
        myFixture.configureByText("B.re", "type t = A.t<caret>;");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.t", ((PsiQualifiedNamedElement) elementAtCaret.getParent().getParent()).getQualifiedName());
    }

}
