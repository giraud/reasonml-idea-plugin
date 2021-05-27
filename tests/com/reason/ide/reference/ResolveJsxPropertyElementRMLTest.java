package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;

public class ResolveJsxPropertyElementRMLTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("X.re", "let make = (~value) => <div/>;");
        configureCode("A.re", "<X value<caret> =1></X>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X.make[value]", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }

    public void test_basic_multiple_props() {
        configureCode("X.re", "let make = (~propA, ~propB, ~propC) => <div/>;");
        configureCode("A.re", "<X propA propB propC<caret> />;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X.make[propC]", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }

    public void test_basic_nested() {
        configureCode("A.re", "module X = { let make = (~value) => <div/>; }; <X value<caret> =1></X>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.make[value]", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }

    public void test_autoclose() {
        configureCode("X.re", "let make = (~value) => <div/>;");
        configureCode("A.re", "<X value<caret> =1/>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X.make[value]", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }

    public void test_open() {
        configureCode("A.re", "module X = { let make = (~value) => <div/>; };");
        configureCode("B.re", "open A; <X value<caret>=1;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.make[value]", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }

    public void test_multiple() {
        configureCode("A.re", "module X = { let make = (~value) => <div/>; }; module Y = { let make = (~value) => <div/>; }; ");
        configureCode("B.re", "open A; <X value<caret>=1;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.make[value]", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }

    public void test_make_make() {
        configureCode("A.re", "module X = { let make = (~value) => <div/>; }; let make = (~value) => <X value<caret> = 1>; ");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.make[value]", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }
}
