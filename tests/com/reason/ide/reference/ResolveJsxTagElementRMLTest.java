package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;

public class ResolveJsxTagElementRMLTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("X.re", "[@react.component] let make = (~value) => <div/>;");
        configureCode("A.re", "<X<caret> ></X>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_not_a_component() {
        configureCode("X.re", "let make = (~value) => <div/>;");
        configureCode("A.re", "<X<caret> ></X>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }

    public void test_nested() {
        configureCode("A.re", "module X = { module Y = { [@react.component] let make = (~value) => <div/>; }; }; <X.Y<caret> ></X>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.Y.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_autoclose() {
        configureCode("X.re", "[@react.component] let make = (~value) => <div/>;");
        configureCode("A.re", "<X<caret> />;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_open() {
        configureCode("A.re", "module X = { [@react.component] let make = (~value) => <div/>; };");
        configureCode("B.re", "open A; <X<caret> ");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }
}
