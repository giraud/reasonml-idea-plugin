package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class ResolveTypeElementTest extends BasePlatformTestCase {

    public void testRml_SameNameWithPath() {
        myFixture.configureByText("A.re", "type t;");
        myFixture.configureByText("B.re", "type t = A.t<caret>;");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.t", ((PsiQualifiedNamedElement) elementAtCaret.getParent().getParent()).getQualifiedName());
    }

    public void testOcl_SameNameWithPath() {
        myFixture.configureByText("A.ml", "type t");
        myFixture.configureByText("B.ml", "type t = A.t<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.t", ((PsiQualifiedNamedElement) elementAtCaret.getParent().getParent()).getQualifiedName());
    }

}
