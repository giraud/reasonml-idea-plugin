package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

public class ResolveUpperElementRESTest extends ORBasePlatformTestCase {
    public void test_basic_file() {
        configureCode("Dimensions.res", "let space = 5;");
        configureCode("Comp.res", "Dimensions<caret>");

        ResFile e = (ResFile) myFixture.getElementAtCaret();
        assertEquals("Dimensions.res", e.getName());
    }

    public void test_interface_implementation() {
        configureCode("A.resi", "type t");
        configureCode("A.res", "type t");
        configureCode("B.res", "A<caret>");

        ResFile e = (ResFile) myFixture.getElementAtCaret();
        assertEquals("A.res", e.getName());
    }

    public void test_let_alias() {
        configureCode("Dimensions.res", "let space = 5");
        configureCode("Comp.res", "let s = Dimensions<caret>.space");

        ResFile e = (ResFile) myFixture.getElementAtCaret();
        assertEquals("Dimensions.res", e.getName());
    }

    public void test_alias() {
        configureCode("A1.res", "module A11 = {}");
        configureCode("A.res", "module A1 = {}");
        configureCode("B.res", "module X = A\n X.A1<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("A.A1", e.getQualifiedName());
    }

    public void test_alias_path_no_resolution() {
        configureCode("A.res", "module X = { module Y = { let z = 1 } }");
        configureCode("B.res", "module C = A.X\n C<caret>.Y");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("B.C", e.getQualifiedName());
    }

    public void test_alias_path_resolution() {
        configureCode("A.res", "module X = { module Y = { let z = 1 } }");
        configureCode("B.res", "module C = A.X\n C.Y<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("A.X.Y", e.getQualifiedName());
    }

    public void test_local_alias() {
        configureCode("Belt.res", "let x = 1");
        configureCode("A.res", "module B = Belt\n B<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("A.B", e.getQualifiedName());
    }

    public void test_alias_interface() {
        configureCode("C.resi", "module A1 = {}");
        configureCode("D.res", "module X = C\n X.A1<caret>;");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("C.A1", e.getQualifiedName());
    }

    public void test_open() {
        configureCode("Belt.res", "module Option = {}");
        configureCode("Dummy.res", "open Belt.Option<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Option", e.getQualifiedName());
        assertEquals("Belt.res", e.getContainingFile().getName());
    }

    public void test_include_path() {
        configureCode("Css_Core.resi", "let display: string => rule");
        configureCode("Css.res", "include Css_Core<caret>\n include Css_Core.Make({})");

        ResInterfaceFile e = (ResInterfaceFile) myFixture.getElementAtCaret();
        assertEquals("Css_Core", e.getQualifiedName());
    }

    public void test_include_alias() {
        configureCode("Css_AtomicTypes.resi", "module Color = { type t }");
        configureCode("Css_Core.resi", "module Types = Css_AtomicTypes");
        configureCode("Css.res", "include Css_Core");
        configureCode("A.res", "Css.Types.Color<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Color", e.getQualifiedName());
    }

    public void test_variant_with_path() {
        configureCode("A.res", "type a = | Variant");
        configureCode("B.res", "type b = | Variant");
        configureCode("C.res", "A.Variant<caret>");

        PsiVariantDeclaration e = (PsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", e.getQualifiedName());
    }

    public void test_variant_module_alias() {
        configureCode("Aaa.res", "type t = | Test");
        configureCode("Bbb.res", "module A = Aaa\n A.Test<caret>");

        PsiVariantDeclaration e = (PsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.t.Test", e.getQualifiedName());
    }

    public void test_variant_module_alias_inner() {
        configureCode("Aaa.res", "module Option = { type t = | Test }");
        configureCode("Bbb.res", "module A = Aaa\n A.Option.Test<caret>");

        PsiVariantDeclaration e = (PsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.t.Test", e.getQualifiedName());
    }

    public void test_variant_constructor() {
        configureCode("A.res", "type a = | Variant(int)");
        configureCode("B.res", "let _ = A.Variant<caret>(1)");

        PsiVariantDeclaration e = (PsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", e.getQualifiedName());
    }

    public void test_exception() {
        myFixture.configureByText("A.res", "exception ExceptionName\n raise(ExceptionName<caret>)");

        PsiException e = (PsiException) myFixture.getElementAtCaret();
        assertEquals("A.ExceptionName", e.getQualifiedName());
    }

    public void test_belt_alias() {
        configureCode("String.res", "type t");
        configureCode("Belt_MapString.res", "type t");
        configureCode("Belt_Map.res", "module String = Belt_MapString");
        configureCode("Belt.res", "module Map = Belt_Map");
        configureCode("A.res", "Belt.Map.String<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", e.getQualifiedName());
    }

    public void test_open_belt_alias() {
        configureCode("String.res", "type t");
        configureCode("Belt_MapString.res", "type t");
        configureCode("Belt_Map.res", "module String = Belt_MapString");
        configureCode("Belt.res", "module Map = Belt_Map");
        configureCode("A.res", "open Belt\n open Map\n String<caret>");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", e.getQualifiedName());
    }

    public void test_pipe_first() {
        configureCode("X.res", "let fn = () => ()");
        configureCode("B.res", "module C = { module X = { let fn = () => () } }");
        configureCode("D.res", "B.C.X.fn()->X<caret>.fn");

        ResFile e = (ResFile) myFixture.getElementAtCaret();
        assertEquals("X", e.getQualifiedName());
    }

    public void test_pipe_last() {
        configureCode("A.res", "module X = {}");
        configureCode("B.res", "module C = { module X = { let fn = () => () } }");
        configureCode("D.res", "B.C.X.fn() |> A.X<caret>.fn");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("A.X", e.getQualifiedName());
    }

    public void test_no_resolution_1() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.res", "module Option = Belt_Option\n module Map = Belt_Map;");
        configureCode("A.res", "let x = (dict, locale) => locale->Belt.Option<caret>.flatMap(dict->Belt.Map.String.get);");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Option", e.getQualifiedName());
    }

    public void test_no_resolution_2() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.res", "module Option = Belt_Option\n module Map = Belt_Map;");
        configureCode("A.res", "let x = (dict, locale) => locale->Belt.Option.flatMap(dict->Belt.Map<caret>.String.get);");

        PsiModule e = (PsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Map", e.getQualifiedName());
    }

    /*
    public void test_functor_inside() {
        configureCode("F.res", "module type S = {module X: {};};\n" +
                "module M = () : S => { module X = {}; };\n" +
                "module A = M({});\n" +
                "module X2 = { module X1 = { module X = {}; }; };\n" +
                "module V = A.X<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("F.S.X",e.getQualifiedName());
    }

    public void test_functor_outside() {
        configureCode("F.res", "module type S = {module X: {};};\n" +
                "module M = () : S => { module X = {}; };\n" +
                "module A = M({});");
        configureCode("B.res", "module X2 = { module X1 = { module X = {}; }; }; module V = F.A.X<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("F.S.X",e.getQualifiedName());
    }
    */
}
