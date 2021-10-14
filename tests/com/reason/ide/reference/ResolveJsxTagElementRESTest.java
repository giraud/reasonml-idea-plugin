package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;

public class ResolveJsxTagElementRESTest extends ORBasePlatformTestCase {
    public void test_basic_let() {
        configureCode("X.res", "@react.component\n let make = (~value) => <div/>;");
        configureCode("A.res", "<X<caret> ></X>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_basic_external() {
        configureCode("X.res", "@react.component\n external make : (~value:string) => React.element = \"Xx\"");
        configureCode("A.res", "<X<caret> ></X>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_not_a_component() {
        configureCode("X.res", "let make = (~value) => <div/>");
        configureCode("A.res", "<X<caret> ></X>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }

    public void test_nested_let() {
        configureCode("A.res", "module X = { module Y = { @react.component\n let make = (~value) => <div/> } }\n <X.Y<caret> ></X>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.Y.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_nested_external() {
        configureCode("A.res", "module X = { module Y = { @react.component\n external make : (~value:string) => React.element = \"XY\" } }\n <X.Y<caret> ></X>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.Y.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_autoclose() {
        configureCode("X.res", "@react.component\n let make = (~value) => <div/>");
        configureCode("A.res", "<X<caret> />");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_open() {
        configureCode("A.res", "module X = { @react.component\n let make = (~value) => <div/> }");
        configureCode("B.res", "open A\n <X<caret> ");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.make", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }
}
