package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;

public class ResolveJsxTagElementRMLTest extends ORBasePlatformTestCase {
    public void test_basic_let() {
        configureCode("X.re", "[@react.component] let make = (~value) => <div/>;");
        configureCode("A.re", "<X<caret> ></X>;");

        PsiLet e = (PsiLet) myFixture.getElementAtCaret();
        assertEquals("X.make", e.getQualifiedName());
    }

    public void test_basic_external() {
        configureCode("X.re", "[@react.component] external make : (~value:string) => React.element = \"Xx\";");
        configureCode("A.re", "<X<caret> ></X>;");

        PsiExternal e = (PsiExternal) myFixture.getElementAtCaret();
        assertEquals("X.make", e.getQualifiedName());
    }

    public void test_not_a_component() {
        configureCode("X.re", "let make = (~value) => <div/>;");
        configureCode("A.re", "<X<caret> ></X>;");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("X", e.getQualifiedName());
    }

    public void test_nested_let() {
        configureCode("A.re", "module X = { module Y = { [@react.component] let make = (~value) => <div/>; }; }; <X.Y<caret> ></X>;");

        PsiLet e = (PsiLet) myFixture.getElementAtCaret();
        assertEquals("A.X.Y.make", e.getQualifiedName());
    }

    public void test_nested_external() {
        configureCode("A.re", "module X = { module Y = { [@react.component] external make : (~value:string) => React.element = \"XY\"; }; }; <X.Y<caret> ></X>;");

        PsiExternal e = (PsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.X.Y.make", e.getQualifiedName());
    }

    public void test_autoclose() {
        configureCode("X.re", "[@react.component] let make = (~value) => <div/>;");
        configureCode("A.re", "<X<caret> />;");

        PsiLet e = (PsiLet) myFixture.getElementAtCaret();
        assertEquals("X.make", e.getQualifiedName());
    }

    public void test_open() {
        configureCode("A.re", "module X = { [@react.component] let make = (~value) => <div/>; };");
        configureCode("B.re", "open A; <X<caret> ");

        PsiLet e = (PsiLet) myFixture.getElementAtCaret();
        assertEquals("A.X.make", e.getQualifiedName());
    }
}
