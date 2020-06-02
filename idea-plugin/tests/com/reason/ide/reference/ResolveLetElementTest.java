package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiQualifiedElement;

public class ResolveLetElementTest extends ORBasePlatformTestCase {

    public void testRml_LocalOpenWithParens() {
        configureCode("A.re", "module A1 = { let a = 3; };");
        configureCode("B.re", "let b = A.(A1.a<caret>);");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }

    public void testOcl_LocalOpenWithParens() {
        configureCode("A.ml", "module A1 = struct let a = 3 end");
        configureCode("B.ml", "let b = A.(A1.a<caret>)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }

    public void testRml_LocalOpenWithParens2() {
        configureCode("A.re", "module A1 = { let a = 3; };");
        configureCode("B.re", "let a = A.A1.(a<caret>);");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiLet) e.getParent()).getQualifiedName());
    }

    public void testLocalOpenWithPipeFirst() {
        configureCode("A.re", "module A1 = { let add = x => x + 3; };");
        configureCode("B.re", "let x = A.A1.(x->add<caret>);");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.A1.add", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }

    public void testRml_InnerScope() {
        configureCode("A.re", "let x = 1; let a = { let x = 2; x<caret> + 10 };");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.x", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }

    public void testOcl_InnerScope() {
        configureCode("A.ml", "let x = 1\nlet a = let x = 2 in x<caret> + 10");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.x", ((PsiLet) e.getParent()).getQualifiedName());
    }

    public void testRml_InnerScopeInFunction() {
        configureCode("A.re", "let x = 1; let fn = { let x = 2; fn1(x<caret>); };");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.fn.x", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }

    public void testOcl_InnerScopeInFunction() {
        configureCode("A.ml", "let x = 1\nlet fn = let x = 2 in fn1 x<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.fn.x", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }

    public void testInnerScopeInImpl() {
        configureCode("A.rei", "let x:int;");
        configureCode("A.re", "let x = 1; let fn = { let foo = 2; fn1(foo<caret>); };");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.fn.foo", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
        assertEquals("A.re", e.getContainingFile().getName());
    }

    public void testLocalModuleAlias() {
        configureCode("A.rei", "let x:int;");
        configureCode("B.re", "module X = A; X.x<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.x", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
        assertEquals("A.rei", e.getContainingFile().getName());
    }
}
