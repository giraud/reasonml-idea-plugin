package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class ResolveUpperElementRMLTest extends ORBasePlatformTestCase {
    @Test
    public void test_basic_file() {
        configureCode("Dimensions.re", "let space = 5;");
        configureCode("Comp.re", "Dimensions<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dimensions.re", ((PsiQualifiedNamedElement) e).getName());
    }

    @Test
    public void test_interface_implementation() {
        configureCode("A.rei", "type t;");
        configureCode("A.re", "type t;");
        configureCode("B.re", "A<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.re", ((PsiNamedElement) e).getName());
    }

    @Test
    public void test_let_alias() {
        configureCode("Dimensions.re", "let space = 5;");
        configureCode("Comp.re", "let s = Dimensions<caret>.space");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Dimensions.re", ((PsiQualifiedNamedElement) elementAtCaret).getName());
    }

    @Test
    public void test_alias() {
        configureCode("A1.re", "module A11 = {};");
        configureCode("A.re", "module A1 = {};");
        configureCode("B.re", "module X = A; X.A1<caret>;");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("A.A1", e.getQualifiedName());
    }

    @Test
    public void test_alias_path_no_resolution() {
        configureCode("A.re", "module X = { module Y = { let z = 1; }; };");
        configureCode("B.re", "module C = A.X; C<caret>.Y");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("B.C", e.getQualifiedName());
    }

    @Test
    public void test_alias_path_resolution() {
        configureCode("A.re", "module X = { module Y = { let z = 1; }; };");
        configureCode("B.re", "module C = A.X; C.Y<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("A.X.Y", e.getQualifiedName());
    }

    @Test
    public void test_local_alias() {
        configureCode("Belt.re", "let x = 1;");
        configureCode("A.re", "module B = Belt; B<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("A.B", e.getQualifiedName());
    }

    @Test
    public void test_alias_interface() {
        configureCode("C.rei", "module A1 = {};");
        configureCode("D.re", "module X = C; X.A1<caret>;");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("C.A1", e.getQualifiedName());
    }

    @Test
    public void test_open() {
        configureCode("Belt.re", "module Option = {}");
        configureCode("Dummy.re", "open Belt.Option<caret>;");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Option", e.getQualifiedName());
        assertEquals("Belt.re", e.getContainingFile().getName());
    }

    @Test
    public void test_include_path() {
        configureCode("Css_Core.rei", "let display: string => rule");
        configureCode("Css.re", "include Css_Core<caret>; include Css_Core.Make({})");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("Css_Core", e.getQualifiedName());
    }

    @Test
    public void test_include_alias() {
        configureCode("Css_AtomicTypes.rei", "module Color = { type t; };");
        configureCode("Css_Core.rei", "module Types = Css_AtomicTypes;");
        configureCode("Css.re", "include Css_Core;");
        configureCode("A.re", "Css.Types.Color<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Color", e.getQualifiedName());
    }

    @Test
    public void test_variant_with_path() {
        configureCode("A.re", "type a = | Variant;");
        configureCode("B.re", "type b = | Variant;");
        configureCode("C.re", "A.Variant<caret>");

        PsiVariantDeclaration e = (PsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", e.getQualifiedName());
    }

    @Test
    public void test_variant_module_alias() {
        configureCode("Aaa.re", "type t = | Test;");
        configureCode("Bbb.re", "module A = Aaa; A.Test<caret>");

        PsiVariantDeclaration e = (PsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.t.Test", e.getQualifiedName());
    }

    @Test
    public void test_variant_module_alias_inner() {
        configureCode("Aaa.re", "module Option = { type t = | Test; }");
        configureCode("Bbb.re", "module A = Aaa; A.Option.Test<caret>");

        PsiVariantDeclaration e = (PsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.t.Test", e.getQualifiedName());
    }

    @Test
    public void test_variant_constructor() {
        configureCode("A.re", "type a = | Variant(int);");
        configureCode("B.re", "let _ = A.Variant<caret>(1)");

        PsiVariantDeclaration e = (PsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", e.getQualifiedName());
    }

    @Test
    public void test_exception() {
        myFixture.configureByText("A.re", "exception ExceptionName; raise(ExceptionName<caret>);");

        PsiException e = (PsiException) myFixture.getElementAtCaret();
        assertEquals("A.ExceptionName", e.getQualifiedName());
    }

    @Test
    public void test_belt_alias() {
        configureCode("String.re", "type t;");
        configureCode("Belt_MapString.re", "type t;");
        configureCode("Belt_Map.re", "module String = Belt_MapString;");
        configureCode("Belt.re", "module Map = Belt_Map;");
        configureCode("A.re", "Belt.Map.String<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", e.getQualifiedName());
    }

    @Test
    public void test_open_belt_alias() {
        configureCode("String.re", "type t;");
        configureCode("Belt_MapString.re", "type t;");
        configureCode("Belt_Map.re", "module String = Belt_MapString;");
        configureCode("Belt.re", "module Map = Belt_Map;");
        configureCode("A.re", "open Belt; open Map; String<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first() {
        configureCode("X.re", "let fn = () => ();");
        configureCode("B.re", "module C = { module X = { let fn = () => (); }; };");
        configureCode("D.re", "B.C.X.fn()->X<caret>.fn");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("X", e.getQualifiedName());
    }

    @Test
    public void test_pipe_last() {
        configureCode("A.re", "module X = {};");
        configureCode("B.re", "module C = { module X = { let fn = () => (); }; };");
        configureCode("D.re", "B.C.X.fn() |> A.X<caret>.fn");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("A.X", e.getQualifiedName());
    }

    @Test
    public void test_no_resolution_1() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString;");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.re", "module Option = Belt_Option; module Map = Belt_Map;");
        configureCode("A.re", "let x = (dict, locale) => locale->Belt.Option<caret>.flatMap(dict->Belt.Map.String.get);");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Option", e.getQualifiedName());
    }

    @Test
    public void test_no_resolution_2() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString;");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.re", "module Option = Belt_Option; module Map = Belt_Map;");
        configureCode("A.re", "let x = (dict, locale) => locale->Belt.Option.flatMap(dict->Belt.Map<caret>.String.get);");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Map", e.getQualifiedName());
    }

    @Test
    public void test_functor_inside() {
        configureCode("F.re", "module type S = {module X: {};};\n" +
                "module M = () : S => { module X = {}; };\n" +
                "module A = M({});\n" +
                "module X2 = { module X1 = { module X = {}; }; };\n" +
                "module V = A.X<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("F.S.X", e.getQualifiedName());
    }

    /* zzz functor
    public void test_functor_outside() {
        configureCode("F.re", "module type S = {module X: {};};\n" +
                "module M = () : S => { module X = {}; };\n" +
                "module A = M({});");
        configureCode("B.re", "module X2 = { module X1 = { module X = {}; }; }; module V = F.A.X<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("F.S.X", e.getQualifiedName());
    }
    */
}
