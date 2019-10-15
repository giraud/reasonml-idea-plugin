package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class ResolveLetElementTest extends BasePlatformTestCase {

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

    public void testLocalOpenWithPipeFirst() {
        myFixture.configureByText("A.re", "module A1 = { let add = x => x + 3; };");
        myFixture.configureByText("B.re", "let x = A.A1.(x->add<caret>);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1.add", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void testInnerScope() {
        myFixture.configureByText("A.re", "let x = 1; let fn = { let x = 2; x<caret> + 10 };");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.fn.x", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void testInnerScopeInFunction() {
        myFixture.configureByText("A.re", "let x = 1; let fn = { let x = 2; fn1(x<caret>); };");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.fn.x", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void testInnerScopeInImpl() {
        myFixture.configureByText("A.rei", "let x:int;");
        myFixture.configureByText("A.re", "let x = 1; let fn = { let foo = 2; fn1(foo<caret>); };");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.fn.foo", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
        assertEquals("A.re", e.getContainingFile().getName());
    }
}
