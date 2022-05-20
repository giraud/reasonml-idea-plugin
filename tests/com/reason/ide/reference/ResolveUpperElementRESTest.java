package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.impl.*;

public class ResolveUpperElementRESTest extends ORBasePlatformTestCase {
    public void test_basic_file() {
        configureCode("Dimensions.res", "let space = 5;");
        configureCode("Comp.res", "Dimensions<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dimensions.res", ((PsiQualifiedNamedElement) e).getName());
    }

    // TODO: Fail sometimes ?
    //public void test_interface_implementation() {
    //    configureCode("A.resi", "type t");
    //    configureCode("A.res", "type t");
    //    configureCode("B.res", "A<caret>");
    //
    //    PsiElement e = myFixture.getElementAtCaret();
    //    assertEquals("A.res", ((PsiNamedElement) e).getName());
    //}

    public void test_let_alias() {
        configureCode("Dimensions.res", "let space = 5");
        configureCode("Comp.res", "let s = Dimensions<caret>.space");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Dimensions.res", ((PsiQualifiedNamedElement) elementAtCaret).getName());
    }

    // TODO Later
    //public void test_alias() {
    //    configureCode("A1.res", "module A11 = {}");
    //    configureCode("A.res", "module A1 = {}");
    //    configureCode("B.res", "module X = A\n X.A1<caret>");
    //
    //    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    //    assertEquals("A.A1", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    //}

    public void test_alias_path_no_resolution() {
        configureCode("A.res", "module X = { module Y = { let z = 1 } }");
        configureCode("B.res", "module C = A.X; C<caret>.Y");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("B.C", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    // TODO Later
    //public void test_alias_path_resolution() {
    //    configureCode("A.res", "module X = { module Y = { let z = 1 } }");
    //    configureCode("B.res", "module C = A.X\n C.Y<caret>");
    //
    //    PsiElement e = myFixture.getElementAtCaret();
    //    assertEquals("A.X.Y", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    //}
    //
    //public void test_local_alias() {
    //    configureCode("Belt.res", "let x = 1");
    //    configureCode("A.res", "module B = Belt\n B<caret>");
    //
    //    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    //    assertEquals("A.B", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    //}
    //
    //public void test_alias_interface() {
    //    configureCode("C.resi", "module A1 = {}");
    //    configureCode("D.res", "module X = C\n X.A1<caret>;");
    //
    //    PsiElement elementAtCaret = myFixture.getElementAtCaret();
    //    assertEquals("C.A1", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    //}

    public void test_open() {
        configureCode("Belt.res", "module Option = {}");
        configureCode("Dummy.res", "open Belt.Option<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Belt.Option", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
        assertEquals("Belt.res", elementAtCaret.getContainingFile().getName());
    }

    public void test_include_path() {
        configureCode("Css_Core.resi", "let display: string => rule");
        configureCode("Css.res", "include Css_Core<caret>\n include Css_Core.Make({})");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Css_Core", ((PsiQualifiedNamedElement) elementAtCaret).getQualifiedName());
    }

    public void test_include_alias() {
        configureCode("Css_AtomicTypes.resi", "module Color = { type t }");
        configureCode("Css_Core.resi", "module Types = Css_AtomicTypes");
        configureCode("Css.res", "include Css_Core");
        configureCode("A.res", "Css.Types.Color<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Color", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void test_variant_with_path() {
        configureCode("A.res", "type a = | Variant");
        configureCode("B.res", "type b = | Variant");
        configureCode("C.res", "A.Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    // TODO Later
    //public void test_variant_module_alias() {
    //    configureCode("Aaa.res", "type t = | Test");
    //    configureCode("Bbb.res", "module A = Aaa\n A.Test<caret>");
    //
    //    PsiElement e = myFixture.getElementAtCaret();
    //    assertEquals("Aaa.t.Test", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    //}
    //
    //public void test_variant_module_alias_inner() {
    //    configureCode("Aaa.res", "module Option = { type t = | Test }");
    //    configureCode("Bbb.res", "module A = Aaa\n A.Option.Test<caret>");
    //
    //    PsiElement e = myFixture.getElementAtCaret();
    //    assertEquals("Aaa.Option.t.Test", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    //}

    public void test_variant_constructor() {
        configureCode("A.res", "type a = | Variant(int)");
        configureCode("B.res", "let _ = A.Variant<caret>(1)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void test_exception() {
        myFixture.configureByText("A.res", "exception ExceptionName\n raise(ExceptionName<caret>)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.ExceptionName", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_belt_alias() {
        configureCode("String.res", "type t");
        configureCode("Belt_MapString.res", "type t");
        configureCode("Belt_Map.res", "module String = Belt_MapString");
        configureCode("Belt.res", "module Map = Belt_Map");
        configureCode("A.res", "Belt.Map.String<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_open_belt_alias() {
        configureCode("String.res", "type t");
        configureCode("Belt_MapString.res", "type t");
        configureCode("Belt_Map.res", "module String = Belt_MapString");
        configureCode("Belt.res", "module Map = Belt_Map");
        configureCode("A.res", "open Belt\n open Map\n String<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_pipe_first() {
        configureCode("X.res", "let fn = () => ()");
        configureCode("B.res", "module C = { module X = { let fn = () => () } }");
        configureCode("D.res", "B.C.X.fn()->X<caret>.fn");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("X", ((PsiQualifiedNamedElement) e).getQualifiedName());
    }

    public void test_pipe_last() {
        configureCode("A.res", "module X = {}");
        configureCode("B.res", "module C = { module X = { let fn = () => () } }");
        configureCode("D.res", "B.C.X.fn() |> A.X<caret>.fn");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_no_resolution_1() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.res", "module Option = Belt_Option\n module Map = Belt_Map;");
        configureCode("A.res", "let x = (dict, locale) => locale->Belt.Option<caret>.flatMap(dict->Belt.Map.String.get);");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Belt.Option", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_no_resolution_2() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.res", "module Option = Belt_Option\n module Map = Belt_Map;");
        configureCode("A.res", "let x = (dict, locale) => locale->Belt.Option.flatMap(dict->Belt.Map<caret>.String.get);");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Belt.Map", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    /*
    public void test_functor_inside() {
        configureCode("F.res", "module type S = {module X: {};};\n" +
                "module M = () : S => { module X = {}; };\n" +
                "module A = M({});\n" +
                "module X2 = { module X1 = { module X = {}; }; };\n" +
                "module V = A.X<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("F.S.X", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_functor_outside() {
        configureCode("F.res", "module type S = {module X: {};};\n" +
                "module M = () : S => { module X = {}; };\n" +
                "module A = M({});");
        configureCode("B.res", "module X2 = { module X1 = { module X = {}; }; }; module V = F.A.X<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("F.S.X", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }
    */
}
