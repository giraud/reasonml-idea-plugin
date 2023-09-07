package com.reason.ide.search.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class ResolveJsxTagElementRMLTest extends ORBasePlatformTestCase {
    @Test
    public void test_basic_let() {
        configureCode("X.re", "[@react.component] let make = (~value) => <div/>;");
        configureCode("A.re", "<X<caret> ></X>;");

        RPsiLet e = (RPsiLet) getNavigationElementAtCaret();
        assertEquals("X.make", e.getQualifiedName());
    }

    @Test
    public void test_basic_external() {
        configureCode("X.re", "[@react.component] external make : (~value:string) => React.element = \"Xx\";");
        configureCode("A.re", "<X<caret> ></X>;");

        RPsiExternal e = (RPsiExternal) getNavigationElementAtCaret();
        assertEquals("X.make", e.getQualifiedName());
    }

    @Test
    public void test_not_a_component() {
        configureCode("X.re", "let make = (~value) => <div/>;");
        configureCode("A.re", "<X<caret> ></X>;");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) getNavigationElementAtCaret();
        assertEquals("X", e.getQualifiedName());
    }

    @Test
    public void test_nested_let() {
        configureCode("A.re", "module X = { module Y = { [@react.component] let make = (~value) => <div/>; }; }; <X.Y<caret> ></X>;");

        RPsiLet e = (RPsiLet) getNavigationElementAtCaret();
        assertEquals("A.X.Y.make", e.getQualifiedName());
    }

    @Test
    public void test_nested_external() {
        configureCode("A.re", "module X = { module Y = { [@react.component] external make : (~value:string) => React.element = \"XY\"; }; }; <X.Y<caret> ></X>;");

        RPsiExternal e = (RPsiExternal) getNavigationElementAtCaret();
        assertEquals("A.X.Y.make", e.getQualifiedName());
    }

    @Test
    public void test_autoclose() {
        configureCode("X.re", "[@react.component] let make = (~value) => <div/>;");
        configureCode("A.re", "<X<caret> />;");

        RPsiLet e = (RPsiLet) getNavigationElementAtCaret();
        assertEquals("X.make", e.getQualifiedName());
    }

    @Test
    public void test_open() {
        configureCode("A.re", "module X = { [@react.component] let make = (~value) => <div/>; };");
        configureCode("B.re", "open A; <X<caret> ");

        RPsiLet e = (RPsiLet) getNavigationElementAtCaret();
        assertEquals("A.X.make", e.getQualifiedName());
    }
}
