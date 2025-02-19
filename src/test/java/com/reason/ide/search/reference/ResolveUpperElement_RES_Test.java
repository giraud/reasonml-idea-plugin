package com.reason.ide.search.reference;

import com.intellij.psi.*;
import com.reason.comp.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

public class ResolveUpperElement_RES_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic_file() {
        configureCode("Dimensions.res", "let space = 5;");
        configureCode("Comp.res", "Dimensions<caret>");

        ResFile e = (ResFile) myFixture.getElementAtCaret();
        assertEquals("Dimensions.res", e.getName());
    }

    @Test
    public void test_interface_implementation() {
        configureCode("A.resi", "type t");
        configureCode("A.res", "type t");
        configureCode("B.res", "A<caret>");

        ResFile e = (ResFile) myFixture.getElementAtCaret();
        assertEquals("A.res", e.getName());
    }

    @Test
    public void test_let_alias() {
        configureCode("Dimensions.res", "let space = 5");
        configureCode("Comp.res", "let s = Dimensions<caret>.space");

        ResFile e = (ResFile) myFixture.getElementAtCaret();
        assertEquals("Dimensions.res", e.getName());
    }

    @Test
    public void test_alias() {
        configureCode("A1.res", "module A11 = {}");
        configureCode("A.res", "module A1 = {}");
        configureCode("B.res", "module X = A\n X.A1<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.A1", e.getQualifiedName());
    }

    @Test
    public void test_alias_path_no_resolution() {
        configureCode("A.res", "module X = { module Y = { let z = 1 } }");
        configureCode("B.res", "module C = A.X\n C<caret>.Y");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("B.C", e.getQualifiedName());
    }

    @Test
    public void test_alias_path_resolution() {
        configureCode("A.res", "module X = { module Y = { let z = 1 } }");
        configureCode("B.res", "module C = A.X\n C.Y<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.X.Y", e.getQualifiedName());
    }

    @Test
    public void test_local_alias() {
        configureCode("Belt.res", "let x = 1");
        configureCode("A.res", "module B = Belt\n B<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.B", e.getQualifiedName());
    }

    @Test
    public void test_alias_interface() {
        configureCode("C.resi", "module A1 = {}");
        configureCode("D.res", "module X = C\n X.A1<caret>;");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("C.A1", e.getQualifiedName());
    }

    @Test
    public void test_include_alias() {
        configureCode("Css_AtomicTypes.resi", "module Color = { type t }");
        configureCode("Css_Core.resi", "module Types = Css_AtomicTypes");
        configureCode("Css.res", "include Css_Core");
        configureCode("A.res", "let t = Css.Types.Color<caret>.t");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Color", e.getQualifiedName());
    }

    @Test
    public void test_function_call() {
        configureCode("AsyncHooks.res", "module XhrAsync = { let make = () => () }");
        configureCode("A.res", "let _ = AsyncHooks.useCancellableRequest(AsyncHooks<caret>.XhrAsync.make)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("AsyncHooks", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_uncurried_function_call() {
        configureCode("A.res", "type t = | Variant");
        configureCode("B.res", "let _ = fn(. A.Variant<caret>)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Variant", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_open() {
        configureCode("Belt.res", "module Option = {}");
        configureCode("Dummy.res", "open Belt.Option<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Option", e.getQualifiedName());
        assertEquals("Belt.res", e.getContainingFile().getName());
    }

    @Test
    public void test_include_path() {
        configureCode("Css_Core.resi", "let display: string => rule");
        configureCode("Css.res", """
                include Css_Core<caret>
                include Css_Core.Make({})
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Css_Core", ((ResInterfaceFile) e).getQualifiedName());
    }

    //region Variants
    @Test
    public void test_local_variant() {
        configureCode("A.res", "type a = | Variant\n let _ = Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Variant", ((RPsiVariantDeclaration) e).getQualifiedName());
    }

    @Test
    public void test_variant_with_path() {
        configureCode("A.res", "type a = | Variant");
        configureCode("B.res", "type b = | Variant");
        configureCode("C.res", "A.Variant<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.Variant", e.getQualifiedName());
    }

    @Test
    public void test_variant_module_alias() {
        configureCode("Aaa.res", "type t = | Test");
        configureCode("Bbb.res", "module A = Aaa\n A.Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Test", e.getQualifiedName());
    }

    @Test
    public void test_variant_module_alias_inner() {
        configureCode("Aaa.res", "module Option = { type t = | Test }");
        configureCode("Bbb.res", "module A = Aaa\n A.Option.Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.Test", e.getQualifiedName());
    }

    @Test
    public void test_variant_constructor() {
        configureCode("A.res", "type a = | Variant(int)");
        configureCode("B.res", "let _ = A.Variant<caret>(1)");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.Variant", e.getQualifiedName());
    }
    //endregion

    //region Poly-variants
    @Test
    public void test_local_poly_variant() {
        configureCode("A.res", "type a = [ | #Variant ]\n let _ = #Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.#Variant", ((RPsiVariantDeclaration) e).getQualifiedName());
    }

    @Test
    public void test_poly_variant_with_path() {
        configureCode("A.res", "type a = [ | #Variant ]");
        configureCode("B.res", "type b = [ | #Variant ]");
        configureCode("C.res", "A.#Variant<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#Variant", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias() {
        configureCode("Aaa.res", "type t = [ | #Test ]");
        configureCode("Bbb.res", "module A = Aaa\n A.#Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.#Test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias_inner() {
        configureCode("Aaa.res", "module Option = { type t = [ | #Test ] }");
        configureCode("Bbb.res", "module A = Aaa\n A.Option.#Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.#Test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_constructor() {
        configureCode("A.res", "type a = [ | #Variant(int) ]");
        configureCode("B.res", "let _ = A.#Variant<caret>(1)");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#Variant", e.getQualifiedName());
    }
    //endregion

    @Test
    public void test_exception() {
        myFixture.configureByText("A.res", "exception ExceptionName\n raise(ExceptionName<caret>)");

        RPsiException e = (RPsiException) myFixture.getElementAtCaret();
        assertEquals("A.ExceptionName", e.getQualifiedName());
    }

    @Test
    public void test_belt_alias() {
        configureCode("String.res", "type t");
        configureCode("Belt_MapString.res", "type t");
        configureCode("Belt_Map.res", "module String = Belt_MapString");
        configureCode("Belt.res", "module Map = Belt_Map");
        configureCode("A.res", "Belt.Map.String<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", e.getQualifiedName());
    }

    @Test
    public void test_open_belt_alias() {
        configureCode("String.res", "type t");
        configureCode("Belt_MapString.res", "type t");
        configureCode("Belt_Map.res", "module String = Belt_MapString");
        configureCode("Belt.res", "module Map = Belt_Map");
        configureCode("A.res", "open Belt\n open Map\n String<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first() {
        configureCode("X.res", "let fn = () => ()");
        configureCode("B.res", "module C = { module X = { let fn = () => () } }");
        configureCode("D.res", "B.C.X.fn()->X<caret>.fn");

        ResFile e = (ResFile) myFixture.getElementAtCaret();
        assertEquals("X", e.getQualifiedName());
    }

    @Test
    public void test_pipe_last() {
        configureCode("A.res", "module X = {}");
        configureCode("B.res", "module C = { module X = { let fn = () => () } }");
        configureCode("D.res", "B.C.X.fn() |> A.X<caret>.fn");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.X", e.getQualifiedName());
    }

    @Test
    public void test_no_resolution_1() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.res", "module Option = Belt_Option\n module Map = Belt_Map;");
        configureCode("A.res", "let x = (dict, locale) => locale->Belt.Option<caret>.flatMap(dict->Belt.Map.String.get);");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Option", e.getQualifiedName());
    }

    @Test
    public void test_no_resolution_2() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.res", "module Option = Belt_Option\n module Map = Belt_Map;");
        configureCode("A.res", "let x = (dict, locale) => locale->Belt.Option.flatMap(dict->Belt.Map<caret>.String.get);");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Map", e.getQualifiedName());
    }

    @Test
    public void test_functor_inside() {
        configureCode("F.res", """
                module type S = { module X: {} }
                module M = () : S => { module X = {} }
                module A = M({})
                module X2 = { module X1 = { module X = {} } }
                module V = A.X<caret>
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("F.M.X", e.getQualifiedName());
    }

    @Test
    public void test_functor_outside() {
        configureCode("F.res", """
                module type S = { module X: {} }
                module M = () : S => { module X = {} }
                module A = M({})
                """);
        configureCode("B.res", "module X2 = { module X1 = { module X = {} } }\n module V = F.A.X<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("F.M.X", e.getQualifiedName());
    }

    @Test
    public void test_with_tag() {
        configureCode("Form.res", "module Styles = {}");
        configureCode("A.res", "module Styles = {}\n let _ = <Form><div className=Styles<caret> /></Form>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.Styles", e.getQualifiedName());
    }

    @Test
    public void test_with_tag_1() {
        configureCode("Form.res", "module Styles = {}");
        configureCode("A.res", "module Styles = {}\n let _ = <Form className=Styles<caret> />");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.Styles", e.getQualifiedName());
    }

    @Test
    public void test_module_signature() {
        configureCode("A.res", """
                module B = {
                  module C = {
                    module type S = {}
                  };
                  module D = C
                };

                module M: B.D.S<caret> = {}
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.B.C.S", e.getQualifiedName());
    }

    @Test
    public void test_module_in_between() {
        configureCode("Styles.res", "let myDiv = 1");
        configureCode("A.res", """
                module Styles = { let myDiv = CssJs.style(. []) }
                
                module Layouts = {}
                
                @react.component
                let make = () => {
                  <div className=Styl<caret>es.myDiv />
                }
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.Styles", e.getQualifiedName());
    }

    @Test
    public void test_not_resolve_alternate_name() {
        configureCode("A.res", "");
        configureCode("B.res", """
                module B1 = {
                  include A
                }
                """);
        configureCode("C.res", """
                module C1 = B.B1<caret>
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("B.B1", e.getQualifiedName());
    }

    @Test
    public void test_with_include() {
        configureCode("A.res", "module A1 = { module A2 = { module A3 = {} } }");
        configureCode("B.res", """
                module B1 = {
                  include A
                }
                """);
        configureCode("C.res", """
                module C1 = B.B1
                module C2 = C1.A1.A2
                module M = C2.A3<caret>
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.A1.A2.A3", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/418
    @Test
    public void test_with_globally_opened_module() {
        myFixture.configureByText(ORConstants.BS_CONFIG_FILENAME, toJson("{ 'name': 'foo', 'bsc-flags': ['-open Core'] }"));
        configureCode("Core.res", "module Console = { }");
        configureCode("A.res", "Console<caret>.log()");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Core.Console", ((RPsiModule) e).getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/426
    @Test
    public void test_alias_resolution_same_file() {
        configureCode("Dummy.res", """
                module A = {
                  module B = {
                    module C = {
                      module D = {
                
                      }
                    }
                  }
                }
                
                module Bbb = A.B
                module Ddd = Bbb.C<caret>.D
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dummy.A.B.C", ((RPsiModule) e).getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/476
    @Test
    public void test_GH_476_and_module() {
        configureCode("Dummy.res", """
                module rec A: {} = { type t = B<caret>.b }
                and B: {type b} = { type b }
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dummy.B", ((RPsiModule) e).getQualifiedName());
    }
}
