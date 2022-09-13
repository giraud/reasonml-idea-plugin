package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;

public class ResolveJsxTagElementRESTest extends ORBasePlatformTestCase {
    public void test_basic_let() {
        configureCode("X.res", "@react.component\n let make = (~value) => <div/>;");
        configureCode("A.res", "<X<caret> ></X>");

        PsiLet e = (PsiLet) myFixture.getElementAtCaret();
        assertEquals("X.make", e.getQualifiedName());
    }

    public void test_basic_external() {
        configureCode("X.res", "@react.component\n external make : (~value:string) => React.element = \"Xx\"");
        configureCode("A.res", "<X<caret> ></X>;");

        PsiExternal e = (PsiExternal) myFixture.getElementAtCaret();
        assertEquals("X.make", e.getQualifiedName());
    }

    public void test_not_a_component() {
        configureCode("X.res", "let make = (~value) => <div/>");
        configureCode("A.res", "<X<caret> ></X>");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("X", e.getQualifiedName());
    }

    public void test_nested_let() {
        configureCode("A.res", "module X = { module Y = { @react.component\n let make = (~value) => <div/> } }\n <X.Y<caret> ></X>");

        PsiLet e = (PsiLet) myFixture.getElementAtCaret();
        assertEquals("A.X.Y.make", e.getQualifiedName());
    }

    public void test_nested_external() {
        configureCode("A.res", "module X = { module Y = { @react.component\n external make : (~value:string) => React.element = \"XY\" } }\n <X.Y<caret> ></X>;");

        PsiExternal e = (PsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.X.Y.make", e.getQualifiedName());
    }

    public void test_autoclose() {
        configureCode("X.res", "@react.component\n let make = (~value) => <div/>");
        configureCode("A.res", "<X<caret> />");

        PsiLet e = (PsiLet) myFixture.getElementAtCaret();
        assertEquals("X.make", e.getQualifiedName());
    }

    public void test_open() {
        configureCode("A.res", "module X = { @react.component\n let make = (~value) => <div/> }");
        configureCode("B.res", "open A\n <X<caret> ");

        PsiLet e = (PsiLet) myFixture.getElementAtCaret();
        assertEquals("A.X.make", e.getQualifiedName());
    }
}
