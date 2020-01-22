package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class ResolveModuleElementTest extends BasePlatformTestCase {

    public void testBasicNull() {
        myFixture.configureByText("Dimensions.re", "let space = 5;");
        myFixture.configureByText("Comp.re", "Dimensions<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Dimensions.re", ((PsiQualifiedNamedElement) elementAtCaret).getName());
    }

    public void testBasic() {
        myFixture.configureByText("Dimensions.re", "let space = 5;");
        myFixture.configureByText("Comp.re", "let D = Dimensions<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Dimensions.re", ((PsiQualifiedNamedElement) elementAtCaret).getName());
    }

    public void testWithAlias() {
        myFixture.configureByText("A1.re", "module A11 = {};");
        myFixture.configureByText("A.re", "module A1 = {};");
        myFixture.configureByText("B.re", "module X = A; X.A1<caret>);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void testWithAliasAndInterface() {
        myFixture.configureByText("C.rei", "module A1 = {};");
        myFixture.configureByText("D.re", "module X = C; X.A1<caret>);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("C.A1", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

}
