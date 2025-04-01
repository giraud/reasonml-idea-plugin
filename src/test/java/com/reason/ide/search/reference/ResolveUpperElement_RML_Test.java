package com.reason.ide.search.reference;

import com.intellij.psi.*;
import com.reason.comp.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

public class ResolveUpperElement_RML_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic_file() {
        configureCode("Dimensions.re", "let space = 5;");
        configureCode("Comp.re", """
                Dimensions<caret>
                module Dimensions = {};
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dimensions.re", ((PsiQualifiedNamedElement) e).getName());
    }

    @Test
    public void test_inner_module() {
        configureCode("A.re", """
                module Dimensions = {};
                Dimensions<caret>
                """);

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("A.Dimensions", e.getQualifiedName());
    }

    @Test
    public void test_interface_implementation() {
        configureCode("A.rei", "type t;");
        configureCode("A.re", "type t;");
        configureCode("B.re", "A<caret>");

        PsiNamedElement e = (PsiNamedElement) myFixture.getElementAtCaret();
        assertEquals("A.rei", e.getName());
    }

    @Test
    public void test_let_alias() {
        configureCode("Dimensions.re", "let space = 5;");
        configureCode("Comp.re", "let s = Dimensions<caret>.space");

        PsiNamedElement e = (PsiNamedElement) myFixture.getElementAtCaret();
        assertEquals("Dimensions.re", e.getName());
    }

    @Test
    public void test_alias() {
        configureCode("A1.re", "module A11 = {};");
        configureCode("A2.re", "module A21 = {};");
        configureCode("A.re", "module A1 = A2;");
        configureCode("B.re", "module X = A; X.A1<caret>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.A1", ((RPsiModule) e).getQualifiedName());
    }

    @Test
    public void test_alias_path_no_resolution() {
        configureCode("A.re", "module X = { module Y = { let z = 1; }; };");
        configureCode("B.re", "module C = A.X; C<caret>.Y");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("B.C", e.getQualifiedName());
    }

    @Test
    public void test_alias_path_resolution() {
        configureCode("A.re", "module X = { module Y = { let z = 1; }; };");
        configureCode("B.re", "module C = A.X; C.Y<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.X.Y", e.getQualifiedName());
    }

    @Test
    public void test_local_alias() {
        configureCode("Belt.re", "let x = 1;");
        configureCode("A.re", "module B = Belt; B<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.B", e.getQualifiedName());
    }

    @Test
    public void test_alias_interface() {
        configureCode("C.rei", "module A1 = {};");
        configureCode("D.re", "module X = C; X.A1<caret>;");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("C.A1", e.getQualifiedName());
    }

    @Test
    public void test_include_alias() {
        configureCode("Css_AtomicTypes.rei", "module Color = { type t; };");
        configureCode("Css_Core.rei", "module Types = Css_AtomicTypes;");
        configureCode("Css.re", "include Css_Core;");
        configureCode("A.re", "let t = Css.Types.Color<caret>.t");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Color", e.getQualifiedName());
    }

    @Test
    public void test_function_call() {
        configureCode("AsyncHooks.re", "module XhrAsync = { let make = () => (); };");
        configureCode("A.re", "let _ = AsyncHooks.useCancellableRequest(AsyncHooks<caret>.XhrAsync.make);");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("AsyncHooks", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_open() {
        configureCode("Belt.re", "module Option = {}");
        configureCode("Dummy.re", "open Belt.Option<caret>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Belt.Option", ((RPsiModule) e).getQualifiedName());
        assertEquals("Belt.re", e.getContainingFile().getName());
    }

    @Test
    public void test_path_from_include() {
        configureCode("Css_Core.rei", "let display: string => rule");
        configureCode("Css.re", "include Css_Core<caret>; include Css_Core.Make({})");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("Css_Core", e.getQualifiedName());
    }

    //region Variants
    @Test
    public void test_local_variant() {
        configureCode("A.re", "type a = | Variant; let _ = Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Variant", ((RPsiVariantDeclaration) e).getQualifiedName());
    }

    @Test
    public void test_variant_with_path() {
        configureCode("A.re", "type a = | Variant;");
        configureCode("B.re", "type b = | Variant;");
        configureCode("C.re", "A.Variant<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.Variant", e.getQualifiedName());
    }

    @Test
    public void test_variant_module_alias() {
        configureCode("Aaa.re", "type t = | Test;");
        configureCode("Bbb.re", "module A = Aaa; A.Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Test", e.getQualifiedName());
    }

    @Test
    public void test_variant_module_alias_inner() {
        configureCode("Aaa.re", "module Option = { type t = | Test; }");
        configureCode("Bbb.re", "module A = Aaa; A.Option.Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.Test", e.getQualifiedName());
    }

    @Test
    public void test_variant_constructor() {
        configureCode("A.re", "type a = | Variant(int);");
        configureCode("B.re", "let _ = A.Variant<caret>(1)");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.Variant", e.getQualifiedName());
    }
    //endregion

    //region Poly-variants
    @Test
    public void test_local_poly_variant() {
        configureCode("A.re", "type a = [ | `Variant ]; let _ = `Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.#Variant", ((RPsiVariantDeclaration) e).getQualifiedName());
    }

    @Test
    public void test_poly_variant_with_path() {
        configureCode("A.re", "type a = [ | `Variant ];");
        configureCode("B.re", "type b = [ | `Variant ];");
        configureCode("C.re", "A.`Variant<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#Variant", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias() {
        configureCode("Aaa.re", "type t = [ | `Test ];");
        configureCode("Bbb.re", "module A = Aaa; A.`Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.#Test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias_inner() {
        configureCode("Aaa.re", "module Option = { type t = [ | `Test ]; };");
        configureCode("Bbb.re", "module A = Aaa; A.Option.`Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.#Test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_constructor() {
        configureCode("A.re", "type a = | `Variant(int);");
        configureCode("B.re", "let _ = A.`Variant<caret>(1);");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#Variant", e.getQualifiedName());
    }
    //endregion

    @Test
    public void test_exception() {
        myFixture.configureByText("A.re", "exception ExceptionName; raise(ExceptionName<caret>);");

        RPsiException e = (RPsiException) myFixture.getElementAtCaret();
        assertEquals("A.ExceptionName", e.getQualifiedName());
    }

    @Test
    public void test_exception_with_path() {
        myFixture.configureByText("A.re", "exception ExceptionName;");
        myFixture.configureByText("B.re", "exception ExceptionName; raise(A.ExceptionName<caret>);");

        RPsiException e = (RPsiException) myFixture.getElementAtCaret();
        assertEquals("A.ExceptionName", e.getQualifiedName());
    }

    @Test
    public void test_belt_alias() {
        configureCode("String.re", "type t;");
        configureCode("Belt_MapString.re", "type t;");
        configureCode("Belt_Map.re", "module String = Belt_MapString;");
        configureCode("Belt.re", "module Map = Belt_Map;");
        configureCode("A.re", "Belt.Map.String<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", e.getQualifiedName());
    }

    @Test
    public void test_open_belt_alias() {
        configureCode("String.re", "type t;");
        configureCode("Belt_MapString.re", "type t;");
        configureCode("Belt_Map.re", "module String = Belt_MapString;");
        configureCode("Belt.re", "module Map = Belt_Map;");
        configureCode("A.re", "open Belt; open Map; String<caret>");

        RPsiQualifiedPathElement e = (RPsiQualifiedPathElement) myFixture.getElementAtCaret();
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

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Option", e.getQualifiedName());
    }

    @Test
    public void test_no_resolution_2() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString;");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.re", "module Option = Belt_Option; module Map = Belt_Map;");
        configureCode("A.re", "let x = (dict, locale) => locale->Belt.Option.flatMap(dict->Belt.Map<caret>.String.get);");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Map", e.getQualifiedName());
    }

    @Test
    public void test_functor_inside() {
        configureCode("F.re", """
                module type S = {module X: {};};
                module M = () : S => { module X = {}; };
                module A = M({});
                module X2 = { module X1 = { module X = {}; }; };
                module V = A.X<caret>
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("F.M.X", e.getQualifiedName());
    }

    @Test
    public void test_functor_outside() {
        configureCode("F.re", """
                module type S = {module X: {};};
                module M = () : S => { module X = {}; };
                module A = M({});
                """);
        configureCode("B.re", "module X2 = { module X1 = { module X = {}; }; }; module V = F.A.X<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("F.M.X", e.getQualifiedName());
    }

    @Test
    public void test_with_tag() {
        configureCode("Form.re", "module Styles = {};");
        configureCode("A.re", "module Styles = {}; let _ = <Form><div className=Styles<caret> /></Form>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.Styles", e.getQualifiedName());
    }

    @Test
    public void test_with_tag_1() {
        configureCode("Form.re", "module Styles = {};");
        configureCode("A.re", "module Styles = {}; let _ = <Form className=Styles<caret> />");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.Styles", e.getQualifiedName());
    }

    @Test
    public void test_module_signature() {
        configureCode("A.re", """
                module B = {
                  module C = {
                    module type S = {};
                  };
                  module D = C;
                };
                
                module M: B.D.S<caret> = {};
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.B.C.S", e.getQualifiedName());
    }

    @Test
    public void test_module_signature_with_open() {
        configureCode("A.re", """
                module B = {
                  module C = { module type S = {}; };
                  module D = C;
                };
                open B;
                open D;
                module M: S<caret> = {};
                """);

        RPsiQualifiedPathElement e = (RPsiQualifiedPathElement) myFixture.getElementAtCaret();
        assertEquals("A.B.C.S", e.getQualifiedName());
    }

    @Test(expected = AssertionError.class)
    public void test_module_signature_incorrect() {
        configureCode("A.re", """
                module B = {
                  module type Intf = {};
                };
                
                module IncorrectImpl : Intf<caret> = {};
                """);

        myFixture.getElementAtCaret();  // not found -> AssertionError
    }

    @Test
    public void test_local_open() {
        configureCode("AsyncHooks.re", "module XhrAsync = {};");
        configureCode("DashboardReducers.re", "type t = | IncrementVersion;");
        configureCode("A.re", "let _ = AsyncHooks.XhrAsync.(dispatch(. DashboardReducers<caret>.IncrementVersion));");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("DashboardReducers", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_not_resolve_alternate_name() {
        configureCode("A.re", "");
        configureCode("B.re", """
                module B1 = {
                  include A;
                };
                """);
        configureCode("C.re", """
                module C1 = B.B1<caret>
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("B.B1", e.getQualifiedName());
    }

    @Test
    public void test_with_include() {
        configureCode("A.re", "module A1 = { module A2 = { module A3 = {}; }; };");
        configureCode("B.re", """
                module B1 = {
                  include A;
                };
                """);
        configureCode("C.re", """
                module C1 = B.B1;
                module C2 = C1.A1.A2;
                module M = C2.A3<caret>
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.A1.A2.A3", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/418
    @Test
    public void test_with_globally_opened_module() {
        myFixture.configureByText(ORConstants.BS_CONFIG_FILENAME, toJson("{ 'name': 'foo', 'bsc-flags': ['-open Core'] }"));
        configureCode("Core.re", "module Console = { };");
        configureCode("A.re", "Console<caret>.log()");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Core.Console", ((RPsiModule) e).getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/426
    @Test
    public void test_alias_resolution_same_file() {
        configureCode("Dummy.re", """
                module A = {
                  module B = {
                    module C = {
                      module D = {};
                    };
                  };
                };
                
                module Bbb = A.B;
                module Ddd = Bbb.C<caret>.D;
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dummy.A.B.C", ((RPsiModule) e).getQualifiedName());
    }

    @Test
    public void test_pervasives_modules() {
        configureCode("JsxDOMC.re", "type style;");
        configureCode("pervasives.re", "module JsxDOM = JsxDOMC;");
        configureCode("A.re", "module A1 = JsxDOM<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Pervasives.JsxDOM", ((RPsiModule) e).getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/476
    @Test
    public void test_GH_476_and_module() {
        configureCode("Dummy.re", """
                module rec A: {} = { type t = B<caret>.b; }
                and B: {type b;} = { type b; };
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dummy.B", ((RPsiModule) e).getQualifiedName());
    }
}
