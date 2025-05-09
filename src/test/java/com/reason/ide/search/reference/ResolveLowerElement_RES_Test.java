package com.reason.ide.search.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

public class ResolveLowerElement_RES_Test extends ORBasePlatformTestCase {
    @Test
    public void test_let_basic() {
        configureCode("A.res", "let x = 1\n let z = x<caret> + 1");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.x", e.getQualifiedName());
    }

    @Test
    public void test_call_function_with_module() {
        configureCode("A.res", "let fn = () => 1");
        configureCode("B.res", "let x = 2");
        configureCode("C.res", "A.fn()\n B.x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.x", e.getQualifiedName());
    }

    @Test
    public void test_let_in_module_binding() {
        configureCode("A.res", "let foo = 2\n module X = { let foo = 1\n let z = foo<caret> }");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.X.foo", e.getQualifiedName());
    }

    @Test
    public void test_let_inner_scope() {
        configureCode("A.res", """
                let x = 1
                let a = {
                  let x = 2
                  x<caret> + 10
                }
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.a.x", e.getQualifiedName());
    }

    @Test
    public void test_inner_scope_in_function() {
        configureCode("A.res", "let x = 1\n let fn = { let x = 2\n fn1(x<caret>)\n }");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.fn.x", e.getQualifiedName());
    }

    @Test
    public void test_inner_scope_in_impl() {
        configureCode("A.rei", "let x:int");
        configureCode("A.res", "let x = 1\n let fn = { let foo = 2\n fn1(foo<caret>) }");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.fn.foo", e.getQualifiedName());
        assertEquals("A.res", e.getContainingFile().getName());
    }

    @Test
    public void test_let_local_module_alias() {
        configureCode("A.rei", "let x:int");
        configureCode("B.res", "let x = 1\n module X = A\n X.x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.x", e.getQualifiedName());
    }

    @Test
    public void test_alias_path() {
        configureCode("A.res", "module W = { module X = { module Y = { module Z = { let z = 1 } } } }");
        configureCode("B.res", "module C = A.W.X\n module D = C.Y.Z\n D.z<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.W.X.Y.Z.z", e.getQualifiedName());
    }

    @Test
    public void test_alias_01() {
        configureCode("A.res", "module Mode = { type t }");
        configureCode("B.res", "module B1 = { module Mode = A.Mode }");
        configureCode("C.res", "B.B1.Mode.t<caret>");        // B.B1.Mode.t -> A.Mode.t

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.Mode.t", e.getQualifiedName());
    }

    @Test
    public void test_alias_02() {
        configureCode("A.res", "module A1 = { module A11 = { type id = string } }");
        configureCode("B.res", "module B1 = A.A1");
        configureCode("C.res", """
                module C1 = B.B1.A11
                type t = C1.id<caret>
                """);

        RPsiQualifiedPathElement e = (RPsiQualifiedPathElement) myFixture.getElementAtCaret();
        assertEquals("A.A1.A11.id", e.getQualifiedName());
    }

    @Test
    public void test_open() {
        configureCode("B.res", "let x = 1");
        configureCode("A.res", "let x = 2\n open B\n x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.x", e.getQualifiedName());
    }

    @Test
    public void test_alias_open() {
        configureCode("B.res", "let x = 1");
        configureCode("A.res", "let x = 2\n module C = B\n open C\n x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.x", e.getQualifiedName());
    }

    @Test
    public void test_type() {
        configureCode("A.res", "type t\n type t' = t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.t", e.getQualifiedName());
        assertTrue(e.isAbstract());
    }

    @Test
    public void test_type_with_path() {
        configureCode("A.res", "type t");
        configureCode("B.res", "type t = A.t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.t", e.getQualifiedName());
    }

    @Test
    public void test_type_with_path_2() {
        configureCode("A.res", "type t\n type y = X.Y.t<caret>");

        assertThrows(AssertionError.class, "element not found in file A.res", () -> myFixture.getElementAtCaret());
    }

    @Test
    public void test_external() {
        configureCode("A.res", "external e : string -> int = \"name\"\n let x = e<caret>");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.e", e.getQualifiedName());
    }

    @Test
    public void test_record_field() {
        configureCode("A.res", """
                type t = { f1: bool, f2: int }
                let x  = { f1: true, f2<caret>: 421 }
                """);

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.t.f2", e.getQualifiedName());
    }

    @Test
    public void test_function() {
        configureCode("A.res", "module B = { let bb = 1; }\n module C = { let cc = x => x }\n let z = C.cc(B.bb<caret>)");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.B.bb", e.getQualifiedName());
    }

    @Test
    public void test_function_open() {
        configureCode("B.res", "module C = { let make = x => x\n let convert = x => x }");
        configureCode("A.res", "open B\n C.make([| C.convert<caret> |])");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.C.convert", e.getQualifiedName());
    }

    @Test
    public void test_param_parenLess() {
        configureCode("A.res", "let add10 = x => x<caret> + 10");

        RPsiParameterDeclaration e = (RPsiParameterDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.add10[x]", e.getQualifiedName());
    }

    @Test
    public void test_include() {
        configureCode("A.res", "module B = { type t; }\n module C = B\n include C\n type x = t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.B.t", e.getQualifiedName());
    }

    @Test
    public void test_include_alias() {
        configureCode("A.res", "module B = { type t }\n module C = B\n include C\n type x = t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.B.t", e.getQualifiedName());
    }

    @Test
    public void test_include_2() {
        configureCode("Css_AtomicTypes.resi", "module Visibility: { type t = [ #visible | #hidden | #collapse ] }");
        configureCode("Css_Legacy_Core.res", "module Types = Css_AtomicTypes");
        configureCode("Css.res", "include Css_Legacy_Core");
        configureCode("A.res", "type layoutRule\n let visibility: [< Css.Types.Length.t | Css.Types.Visibility.t<caret> ] => layoutRule");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Visibility.t", e.getQualifiedName());
    }

    @Test
    public void test_include_qualified() {
        configureCode("A.res", "module B = { module C = { type t } }\n module D = B\n include D.C");
        configureCode("C.res", "type t = A.t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.B.C.t", e.getQualifiedName());
    }

    @Test
    public void test_module_signature() {
        configureCode("A.res", "module B: { type t\n let toString: t => string }\n module C: { type t\n let toString: t<caret> => string }");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.C.t", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first() {
        configureCode("Css.mli", "val px: int -> string");
        configureCode("A.res", "Dimensions.spacing.small->Css.px<caret>");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Css.px", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first_open() {
        configureCode("Css.mli", "val px: int -> string");
        configureCode("A.res", "let make = () => { open Css; Dimensions.spacing.small->px<caret> }");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Css.px", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first_open_2() {
        configureCode("Core.res", "module Async = { let get = x => x }");
        configureCode("A.res", "open Core.Async\n request->get<caret>(\"windows/settings\")");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Core.Async.get", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first_open_with_path() {
        configureCode("Css.mli", "module Rule = { val px: int => string }");
        configureCode("A.res", "let make = () => { open Css\n Dimensions.spacing.small->Rule.px<caret> }");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Css.Rule.px", e.getQualifiedName());
    }

    @Test
    public void test_multiple_module() {
        configureCode("Command.res", "module Settings = { module Action = { let convert = x => x } }");
        configureCode("A.res", "module C = Y\n open Command\n Settings.Action.convert<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Command.Settings.Action.convert", e.getQualifiedName());
    }

    //region Variants
    @Test
    public void test_variant_constructor() {
        configureCode("B.res", "let convert = x => x");
        configureCode("A.res", "X.Variant(B.convert<caret>())");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.convert", e.getQualifiedName());
    }

    @Test
    public void test_variant_constructor_tuple() {
        configureCode("B.res", "type t('a) = | Variant('a, 'b)");
        configureCode("A.res", "let x = 1\n B.Variant(X.Y, x<caret>)");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.x", e.getQualifiedName());
    }
    //endregion

    //region Poly-variants
    @Test
    public void test_local_poly_variant() {
        configureCode("A.res", "type a = [ | #variant ]\n let _ = #variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.#variant", ((RPsiVariantDeclaration) e).getQualifiedName());
    }

    @Test
    public void test_poly_variant_with_path() {
        configureCode("A.res", "type a = [ | #variant ]");
        configureCode("B.res", "type b = [ | #variant ]");
        configureCode("C.res", "A.#variant<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#variant", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias() {
        configureCode("Aaa.res", "type t = [ | #test ]");
        configureCode("Bbb.res", "module A = Aaa\n A.#test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.#test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias_inner() {
        configureCode("Aaa.res", "module Option = { type t = [ | #test ] }");
        configureCode("Bbb.res", "module A = Aaa\n A.Option.#test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.#test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_constructor() {
        configureCode("A.res", "type a = [ | #variant(int) ]");
        configureCode("B.res", "let _ = A.#variant<caret>(1)");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#variant", e.getQualifiedName());
    }
    //endregion

    @Test
    public void test_open_include() {
        configureCode("Css_Core.res", "let fontStyle = x => x");
        configureCode("Css.res", "include Css_Core");
        configureCode("A.res", "open Css\n fontStyle<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Css_Core.fontStyle", e.getQualifiedName());
    }

    @Test
    public void test_open_include_deep() {
        configureCode("Css_Rule.res", "let fontStyle = x => x");
        configureCode("Css_Core.res", "module Rules = { include Css_Rule }");
        configureCode("Css.res", "include Css_Core");
        configureCode("A.res", "open Css.Rules\n fontStyle<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Css_Rule.fontStyle", e.getQualifiedName());
    }

    @Test
    public void test_function_call_1() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.res", "module Option = Belt_Option\n module Map = Belt_Map");
        configureCode("A.res", "let x = (dict, locale) => locale->Belt.Option.flatMap<caret>(dict->Belt.Map.String.get)");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Belt_Option.flatMap", e.getQualifiedName());
    }

    @Test
    public void test_function_call_2() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.res", "module Option = Belt_Option\n module Map = Belt_Map;");
        configureCode("A.res", "let x = (dict, locale) => locale->Belt.Option.flatMap(dict->Belt.Map.String.get<caret>)");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Belt_MapString.get", e.getQualifiedName());
    }

    @Test
    public void test_function_call_3() {
        configureCode("Storybook.res", "external action: string => unit => unit");
        configureCode("A.res", "let _ = Storybook.action<caret>(\"Cancel\")()");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Storybook.action", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_functor_body() {
        configureCode("A.res", "module Make = (M:I) => { let a = 3 }");
        configureCode("B.res", "module Instance = A.Make({})\n let b = Instance.a<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Make.a", e.getQualifiedName());
    }

    @Test
    public void test_file_include_functor() {
        configureCode("A.res", "module Make = (M:I) => { let a = 3 }\n include Make({})");
        configureCode("B.res", "let b = A.a<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Make.a", e.getQualifiedName());
    }

    @Test
    public void test_functor_result_with_alias() {
        configureCode("A.res", "module type Result = { let a: int }");
        configureCode("B.res", "module T = A\n module Make = (M:Intf): T.Result => { let b = 3 }");
        configureCode("C.res", "module Instance = B.Make({})\n let c = Instance.a<caret>;");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Result.a", e.getQualifiedName());
    }

    @Test
    public void test_path_functor() {
        configureCode("pervasives.mli", "external compare : 'a -> 'a -> int = \"%compare\"");
        configureCode("A.res", "module B = X.Functor({ let cmp = Pervasives.compare<caret> })");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("Pervasives.compare", e.getQualifiedName());
    }

    @Test
    public void test_path_functor_1() {
        configureCode("E.res", """
                module type E1Intf = {
                  type t
                }
                """);
        configureCode("D.res", """
                module type D1Intf = {
                  let make: unit => unit
                }
                
                module Make = (M: E.E1Intf): D1Intf => {
                  let make = () => ()
                }
                """);
        configureCode("C.res", "module C1 = D");
        configureCode("B.res", "module Instance = C.C1.Make(X)");
        configureCode("A.res", "let _ = B.Instance.make<caret>");

        PsiElement e = myFixture.getElementAtCaret();

        assertEquals("D.D1Intf.make", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_global_local() {
        configureCode("Styles.res", "");
        configureCode("B.res", "");
        configureCode("A.res", """
                open B
                
                module Styles = {
                  let x = 1
                }
                
                let x = Styles.x<caret>
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Styles.x", e.getQualifiedName());
    }

    //region record
    @Test
    public void test_record_type() {
        configureCode("A.res", """
                type t = { f1: bool, f2: int }
                let x = { f1: true, f2<caret>: 421 }
                """);

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.t.f2", e.getQualifiedName());
    }

    @Test
    public void test_record() {
        configureCode("B.res", """
                let b = { a: 1, b: 2 }
                b<caret>
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.b", e.getQualifiedName());
    }

    @Test
    public void test_record_l1() {
        configureCode("B.res", "let b = { a: 1, b: 2 }\n b.b<caret>");

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("B.b.b", e.getQualifiedName());
    }

    @Test
    public void test_record_l3() {
        configureCode("A.res", """
                let a = { b: { c: { d: 1 } } }
                a.b.c.d<caret>
                """);

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.a.b.c.d", e.getQualifiedName());
    }
    //endregion

    //region object
    @Test
    public void test_object_1() {
        configureCode("A.res", """
                let a = { "b": 1, "c": 2 }
                a["b<caret>"]
                """);

        RPsiObjectField e = (RPsiObjectField) myFixture.getElementAtCaret();
        assertEquals("A.a.b", e.getQualifiedName());
    }

    @Test
    public void test_object_2() {
        configureCode("A.res", """
                let a = { "b": 1, "c": 2 }
                let _ = a["b<caret>"]["c"]
                """);

        RPsiObjectField e = (RPsiObjectField) myFixture.getElementAtCaret();
        assertEquals("A.a.b", e.getQualifiedName());
    }

    @Test
    public void test_object_3() {
        configureCode("A.res", """
                let a = { "b": { "c": { "d": 1 } } }
                a["b"]["c"]["d<caret>"]
                """);

        RPsiObjectField e = (RPsiObjectField) myFixture.getElementAtCaret();
        assertEquals("A.a.b.c.d", e.getQualifiedName());
    }

    @Test
    public void test_object_4() {
        configureCode("B.res", """
                type t = {
                  "x": {
                    "y": string
                  }
                }
                """);
        configureCode("A.res", """
                let _ = (p0: B.t) => p0["x"]["y"<caret>]
                """);


        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("B.t.x.y", ((RPsiObjectField) e).getQualifiedName());
    }

    @Test
    public void test_deep_open() {
        configureCode("A.res", """
                let oo = {"first": {"deep": true}, "deep": {"other": {"asd": 1} } }
                """);
        configureCode("B.res", """
                open A
                oo["deep"]["other"<caret>]
                """);

        RPsiObjectField e = (RPsiObjectField) myFixture.getElementAtCaret();
        assertEquals("A.oo.deep.other", e.getQualifiedName());
    }
    //endregion

    @Test
    public void test_alias_of_alias() {
        configureCode("A.res", """
                module A1 = {
                    module A2 = {
                      let id = "_new_"
                    }
                }
                """);

        configureCode("B.res", """
                module B1 = {
                  module B2 = {
                    module B3 = {
                      let id = A.A1.A2.id
                    }
                  }
                }
                
                module B4 = {
                  include A
                  module B5 = B1.B2
                }
                """);

        configureCode("C.res", """
                module C1 = B.B4
                module C2 = C1.B5.B3
                let _ = C2.id<caret>
                """);

        PsiElement e = myFixture.getElementAtCaret();

        assertEquals("B.B1.B2.B3.id", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_parameter_signature() {
        configureCode("A.res", """
                module A1 = {
                  module A2 = {
                    type t = { a: int, b: int }
                  }
                }
                let x = (p0: A1.A2.t) => { p0.a<caret> }
                """);

        PsiElement e = myFixture.getElementAtCaret();

        assertEquals("A.A1.A2.t.a", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_pervasives() {
        configureCode("JsxDOMC.res", "type style");
        configureCode("pervasives.res", "module JsxDOM = JsxDOMC");
        configureCode("A.res", "module A1 = JsxDOM.style<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("JsxDOMC.style", ((RPsiType) e).getQualifiedName());

    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/452
    @Test
    public void test_GH_452_resolve_unpacked_module() {
        configureCode("A.res", """
                module type I = {
                  let x: int
                }
                
                let x = (~p: module(I)) => {
                    module S = unpack(p)
                    S.x<caret>
                };
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.I.x", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/452
    @Test
    public void test_GH_452_resolve_unpacked_module_inner_module() {
        configureCode("A.res", """
                module B = {
                  module type I = {
                    let fn: int => unit
                  }
                }
                """);
        configureCode("C.res", """
                let x = (~p: module(A.B.I)) => {
                    module S = unpack(p)
                    S.fn<caret>(1)
                };
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.B.I.fn", e.getQualifiedName());
    }

    @Test
    public void test_GH_167_deconstruction() {
        configureCode("A.res", """
        let (count, setCount) = React.useState(() => 0)
        setCount<caret>(1)
        """);

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals(12, elementAtCaret.getTextOffset());
        assertInstanceOf(elementAtCaret, RPsiLowerName.class);
    }

    @Test
    public void test_GH_303() {
        configureCode("B.res", "type t1 = {bar: string}");
        configureCode("A.res", "type t = {bar: string}\n let bar = item => item.bar<caret>");

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.t.bar", e.getQualifiedName());
    }

    @Test
    public void test_GH_303_2() {
        configureCode("B.res", "type t1 = {bar:string}");
        configureCode("A.res", "type t = {bar: string}\n let bar<caret> = item => item.bar");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.bar", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/358
    @Test
    public void test_GH_358() {
        configureCode("A.res", """
                let clearPath = () => ()
                
                module Xxx = {
                  type t = | ClearPath
                  let clearPath = () => ()
                }
                
                let reducer = x => switch x {
                  | Xxx.ClearPath => clearPath<caret>()
                }
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.clearPath", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/461
    @Test
    public void test_GH_461_parameter_type() {
        configureCode("A.res", """
                type store = {x: int}
                let fn = (store: store<caret>) => store.x
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.store", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/475
    @Test
    public void test_GH_475_stack_overflow() {
        configureCode("A.res", """
                type t = { id: string, name: string }
                
                let isSame = (item:t, item':t) => {
                  let sameId = item.id == item'.id
                  let sameName = item.name<caret> == item'.name
                  sameId && sameName
                }
                """);

        PsiElement e = myFixture.getElementAtCaret();  // must not throw StackOverflowError
        assertEquals("A.t.name", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/476
    @Test
    public void test_GH_476_and_let() {
        configureCode("A.res", """
                let rec x = () => y<caret>()
                /* comment */
                and z = () => x()
                and y = () => x()
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.y", ((RPsiLet) e).getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/476
    @Test
    public void test_GH_476_and_type() {
        configureCode("A.res", """
                type rec x = y<caret>
                /* comment */
                and y = string
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.y", ((RPsiType) e).getQualifiedName());
    }
}
