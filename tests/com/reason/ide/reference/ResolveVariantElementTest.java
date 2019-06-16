package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class ResolveVariantElementTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testWithAlias() {
        myFixture.configureByText("A.re", "type a = | Variant;");
        myFixture.configureByText("B.re", "type b = | Variant;");
        myFixture.configureByText("C.re", "A.Variant<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

}
