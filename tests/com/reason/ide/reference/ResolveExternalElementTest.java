package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.reason.ide.ORBasePlatformTestCase;

public class ResolveExternalElementTest extends ORBasePlatformTestCase {

    public void testLocalOpenWithParens() {
        configureCode("A.re", "module A1 = { external a : int = \"\"; };");
        configureCode("B.re", "let b = A.(A1.a<caret>);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void testLocalOpenWithParens2() {
        myFixture.configureByText("A.re", "module A1 = { external a : int = \"\"; };");
        myFixture.configureByText("B.re", "let a = A.A1.(a<caret>);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void testLocalOpenWithPipeFirst() {
        myFixture.configureByText("A.re", "module A1 = { external add : int => int = \"\"; };");
        myFixture.configureByText("B.re", "let x = A.A1.(x->add<caret>);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1.add", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }
}
