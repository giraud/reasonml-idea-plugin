package com.reason.ide.search.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class ResolveLowerElement_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void test_let_basic() {
        configureCode("A.ml", "let x = 1\n let z = x<caret> + 1");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.x", e.getQualifiedName());
    }

    @Test
    public void test_call_function_with_module() {
        configureCode("A.ml", "let fn () = 1");
        configureCode("B.ml", "let x = 2");
        configureCode("C.ml", "let _ = A.fn()\n let _ = B.x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.x", e.getQualifiedName());
    }

    @Test
    public void test_let_in_module_binding() {
        configureCode("A.ml", "let foo = 2 module X = struct let foo = 1 let z = foo<caret> end");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.X.foo", e.getQualifiedName());
    }

    @Test
    public void test_let_inner_scope() {
        configureCode("A.ml", "let x = 1\n let a = let x = 2 in x<caret> + 10");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.a.x", e.getQualifiedName());
    }

    @Test
    public void test_inner_scope_in_function() {
        configureCode("A.ml", "let x = 1\n let fn = let x = 2 in fn1 x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.fn.x", e.getQualifiedName());
    }

    @Test
    public void test_inner_scope_in_impl() {
        configureCode("A.mli", "val x:int");
        configureCode("A.ml", "let x = 1\n let fn = let foo = 2 in fn1 foo<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.fn.foo", e.getQualifiedName());
        assertEquals("A.ml", e.getContainingFile().getName());
    }

    @Test
    public void test_let_local_module_alias() {
        configureCode("A.mli", "val x:int");
        configureCode("B.ml", "let x = 1\n module X = A\n let _ = X.x<caret>");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("A.x", e.getQualifiedName());
    }

    @Test
    public void test_alias_path() {
        configureCode("A.ml", "module X = struct module Y = struct let z = 1 end end");
        configureCode("B.ml", "module C = A.X.Y\n let _ = C.z<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.X.Y.z", e.getQualifiedName());
    }

    @Test
    public void test_alias_01() {
        configureCode("A.ml", "module Mode = struct type t end");
        configureCode("B.ml", "module B1 = struct module Mode = A.Mode end");
        configureCode("C.ml", "type t = B.B1.Mode.t<caret>");        // B.B1.Mode.t -> A.Mode.t

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.Mode.t", e.getQualifiedName());
    }

    @Test
    public void test_alias_02() {
        configureCode("A.ml", "module A1 = struct module A11 = struct type t = string end end");
        configureCode("B.ml", "module B1 = A.A1");
        configureCode("C.ml", """
                module C1 = B.B1.A11
                type t = C1.t<caret>
                """);

        RPsiQualifiedPathElement e = (RPsiQualifiedPathElement) myFixture.getElementAtCaret();
        assertEquals("A.A1.A11.t", e.getQualifiedName());
    }

    @Test
    public void test_open() {
        configureCode("B.ml", "let x = 1");
        configureCode("A.ml", "let x = 2 open B let _ = x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.x", e.getQualifiedName());
    }

    @Test
    public void test_alias_open() {
        configureCode("B.ml", "let x = 1");
        configureCode("A.ml", "let x = 2 module C = B open C let _ = x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.x", e.getQualifiedName());
    }

    @Test
    public void test_let_local_open_parens() {
        configureCode("A.ml", "module A1 = struct let a = 1 end");
        configureCode("B.ml", "let a = 2 let b = A.(A1.a<caret>)");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.A1.a", e.getQualifiedName());
    }

    @Test
    public void test_let_local_open_parens_2() {
        configureCode("A.ml", "module A1 = struct let a = 3 end");
        configureCode("B.ml", "let a = A.A1.(a<caret>)");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.A1.a", e.getQualifiedName());
    }

    @Test
    public void test_type() {
        configureCode("A.ml", "type t type t' = t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.t", e.getQualifiedName());
        assertTrue(e.isAbstract());
    }

    @Test
    public void test_type_with_path() {
        configureCode("A.ml", "type t");
        configureCode("B.ml", "type t = A.t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.t", e.getQualifiedName());
    }

    @Test
    public void test_type_with_path_2() {
        configureCode("A.ml", "type t\n type y = X.Y.t<caret>");

        assertThrows(AssertionError.class, "element not found in file A.ml", () -> myFixture.getElementAtCaret());
    }

    @Test
    public void test_external() {
        configureCode("A.ml", "external e : string -> int = \"name\"\n let x = e<caret>");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.e", e.getQualifiedName());
    }

    @Test
    public void test_function() {
        configureCode("A.ml", "module B = struct let bb = 1 end\n module C = struct let cc x = x end let z = C.cc(B.bb<caret>)");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.B.bb", e.getQualifiedName());
    }

    @Test
    public void test_function_open() {
        configureCode("B.ml", "module C = struct let make x = x\n let convert x = x end");
        configureCode("A.ml", "open B\n let _ = C.make([| C.convert<caret> |])");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.C.convert", e.getQualifiedName());
    }

    @Test
    public void test_param_parenLess() {
        configureCode("A.ml", "let add10 x = x<caret> + 10;");

        RPsiParameterDeclaration e = (RPsiParameterDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.add10[x]", e.getQualifiedName());
    }

    @Test
    public void test_local_open() {
        configureCode("A.ml", "module A1 = struct external a : int = \"\" end");
        configureCode("B.ml", "let _ = let open A.A1 in a<caret>");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.A1.a", e.getQualifiedName());
    }

    @Test
    public void test_local_open_2() {
        configureCode("A.ml", """
                module A1 = struct
                  type t = | Variant
                  let toString = x => x
                end
                """);
        configureCode("B.ml", "let _ = let open A.A1 in Variant |. toString<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.A1.toString", e.getQualifiedName());
    }

    @Test
    public void test_include() {
        configureCode("A.ml", "module B = struct type t end\n module C = B\n include C\n type x = t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.B.t", e.getQualifiedName());
    }

    @Test
    public void test_include_alias() {
        configureCode("A.ml", """
                module B = struct
                  type t
                end
                module C = B
                include C
                type x = t<caret>
                """);

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.B.t", e.getQualifiedName());
    }

    @Test
    public void test_include_2() {
        configureCode("Css_AtomicTypes.mli", "module Visibility: sig\n type t = [ | `visible | `hidden | `collapse]\n end");
        configureCode("Css_Legacy_Core.ml", "module Types = Css_AtomicTypes");
        configureCode("Css.ml", "include Css_Legacy_Core");
        configureCode("A.ml", "type layoutRule\n let visibility: [< Css.Types.Length.t | Css.Types.Visibility.t<caret> ] -> layoutRule");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Visibility.t", e.getQualifiedName());
    }

    @Test
    public void test_include_qualified() {
        configureCode("A.ml", "module B = struct module C = struct type t end end\n module D = B\n include D.C");
        configureCode("C.ml", "type t = A.t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.B.C.t", e.getQualifiedName());
    }

    @Test
    public void test_module_signature() {
        configureCode("A.ml", """
                module B = sig
                  type t
                  let toString: t -> string
                end
                module C = sig
                  type t
                  let toString: t<caret> -> string
                end
                """);

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.C.t", e.getQualifiedName());
    }

    @Test
    public void test_let_Local_open_pipe_first() {
        configureCode("A.ml", "module A1 = struct let add x = x + 3 end");
        configureCode("B.ml", "let x = let open A.A1 in x |. add<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.A1.add", e.getQualifiedName());
    }

    @Test
    public void test_external_local_open_pipe_first() {
        configureCode("A.ml", "module A1 = struct external add : int -> int = \"\" end");
        configureCode("B.ml", "let x = let open A.A1 in x |. add<caret>);");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.A1.add", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first() {
        configureCode("Css.mli", "val px: int -> string");
        configureCode("A.ml", "let _ = Dimensions.spacing.small |. Css.px<caret>");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Css.px", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first_open() {
        configureCode("Css.mli", "val px: int -> string");
        configureCode("A.ml", "let make () = let open Css in Dimensions.Spacing.small |. px<caret>");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Css.px", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first_open_2() {
        configureCode("Core.ml", """
                module Async = struct
                  let get x = x
                end
                """);
        configureCode("A.ml", """
                open Core.Async
                let _ = request |. get<caret> "windows/settings"
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Core.Async.get", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first_open_with_path() {
        configureCode("Css.mli", "module Rule = struct val px: int -> string end");
        configureCode("A.ml", "let make () = let open Css in Dimensions.spacing.small |. Rule.px<caret>");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Css.Rule.px", e.getQualifiedName());
    }

    @Test
    public void test_multiple_module() {
        configureCode("Command.ml", "module Settings = struct module Action = struct let convert x = x end end");
        configureCode("A.ml", "module C = Y\n open Command\n Settings.Action.convert<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Command.Settings.Action.convert", e.getQualifiedName());
    }

    //region Variants
    @Test
    public void test_variant_constructor() {
        configureCode("B.ml", "let convert x = x");
        configureCode("A.ml", "let _ = X.Variant(B.convert<caret> 1)");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.convert", e.getQualifiedName());
    }

    @Test
    public void test_variant_constructor_tuple() {
        configureCode("B.ml", "type ('a, 'b) t = | Variant of 'a * 'b");
        configureCode("A.ml", "let x = 1\n let _ = B.Variant(X.Y, x<caret>)");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.x", e.getQualifiedName());
    }
    //endregion

    //region Poly-variants
    @Test
    public void test_local_poly_variant() {
        configureCode("A.ml", "type a = [ | `variant ]\n let _ = `variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.#variant", ((RPsiVariantDeclaration) e).getQualifiedName());
    }

    @Test
    public void test_poly_variant_with_path() {
        configureCode("A.ml", "type a = [ | `variant ]");
        configureCode("B.ml", "type b = [ | `variant ]");
        configureCode("C.ml", "A.`variant<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#variant", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias() {
        configureCode("Aaa.ml", "type t = [ | `test ]");
        configureCode("Bbb.ml", "module A = Aaa\nlet _ = A.`test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.#test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias_inner() {
        configureCode("Aaa.ml", "module Option = struct type t = [ | `test ] end");
        configureCode("Bbb.ml", "module A = Aaa\nlet _ = A.Option.`test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.#test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_constructor() {
        configureCode("A.ml", "type a = | `variant of int");
        configureCode("B.ml", "let _ = A.`variant<caret> 1");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#variant", e.getQualifiedName());
    }
    //endregion

    @Test
    public void test_open_include() {
        configureCode("Css_Core.ml", "let fontStyle x = x");
        configureCode("Css.ml", "include Css_Core");
        configureCode("A.ml", "open Css\n let _ = fontStyle<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Css_Core.fontStyle", e.getQualifiedName());
    }

    @Test
    public void test_open_include_deep() {
        configureCode("Css_Rule.ml", "let fontStyle x = x");
        configureCode("Css_Core.ml", "module Rules = struct include Css_Rule end");
        configureCode("Css.ml", "include Css_Core");
        configureCode("A.ml", "open Css.Rules\n let _ = fontStyle<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Css_Rule.fontStyle", e.getQualifiedName());
    }

    @Test
    public void test_resolution_1() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.ml", "module Option = Belt_Option\n module Map = Belt_Map");
        configureCode("A.ml", "let fn dict locale = (locale |. Belt.Option.flatMap<caret>) (dict |. Belt.Map.String.get)");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Belt_Option.flatMap", e.getQualifiedName());
    }

    @Test
    public void test_resolution_2() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.ml", "module Option = Belt_Option\n module Map = Belt_Map");
        configureCode("A.ml", "let x dict locale = (locale |. Belt.Option.flatMap) (dict |. Belt.Map.String.get<caret>)");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Belt_MapString.get", e.getQualifiedName());
    }

    @Test
    public void test_functor_body() {
        configureCode("A.ml", "module Make(M:I) = struct let a = 3 end");
        configureCode("B.ml", "module Instance = A.Make(struct end)\n let b = Instance.a<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Make.a", e.getQualifiedName());
    }

    @Test
    public void test_file_include_functor() {
        configureCode("A.ml", """
                module Make (M:I) = struct
                  let a = 3
                end
                include Make(struct end)
                """);
        configureCode("B.ml", "let b = A.a<caret>;");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Make.a", e.getQualifiedName());
    }

    @Test
    public void test_functor_result_with_alias() {
        configureCode("A.ml", "module type Result = sig let a: int end");
        configureCode("B.ml", "module T = A\n module Make(M:Intf): T.Result = struct let b = 3 end");
        configureCode("C.ml", "module Instance = B.Make(struct end)\n let c = Instance.a<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Result.a", e.getQualifiedName());
    }

    @Test
    public void test_path_functor() {
        configureCode("pervasives.mli", "external compare : 'a -> 'a -> int = \"%compare\"");
        configureCode("A.ml", "module B = X.Functor(struct let cmp = Pervasives.compare<caret> end)");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("Pervasives.compare", e.getQualifiedName());
    }

    @Test
    public void test_parameter_signature() {
        configureCode("A.ml", """
                module A1 = struct
                  type t = { a: int; b: int }
                end
                let x (p0: A1.t) = p0.a<caret>
                """);

        PsiElement e = myFixture.getElementAtCaret();

        assertEquals("A.A1.t.a", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    //region record
    @Test
    public void test_record_type() {
        configureCode("A.ml", """
                type t = { f1: bool; f2: int; }
                let x = { f1 = true; f2<caret> = 421 }
                """);

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.t.f2", e.getQualifiedName());
    }

    @Test
    public void test_record() {
        configureCode("B.ml", """
                let b = { a = 1; b = 2; }
                let _ = b<caret>
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.b", e.getQualifiedName());
    }

    @Test
    public void test_record_l1() {
        configureCode("B.ml", """
                let b = { a = 1; b = 2; }
                let _ = b.b<caret>
                """);

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("B.b.b", e.getQualifiedName());
    }

    @Test
    public void test_record_l3() {
        configureCode("A.ml", """
                let a = { b = { c = { d = 1 } } }
                let _ = a.b.c.d<caret>
                """);

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.a.b.c.d", e.getQualifiedName());
    }
    //endregion

    // https://github.com/giraud/reasonml-idea-plugin/issues/452
    @Test
    public void test_GH_452_resolve_unpacked_module() {
        configureCode("A.ml", """
                module type I = sig
                  val x: int
                end

                let x ~p:(p: (module I)) =
                    let module S = (val p) in
                    S.x<caret>
                """);

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("A.I.x", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/452
    @Test
    public void test_GH_452_resolve_unpacked_module_inner_module() {
        configureCode("A.ml", """
                module B = struct
                  module type I = sig
                    val fn: int -> unit
                  end
                end
                """);
        configureCode("C.ml", """
                let x ~p:(p:(module A.B.I)) =
                    let module S = (val p) in
                    S.fn<caret>(1)
                };
                """);

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("A.B.I.fn", e.getQualifiedName());
    }

    @Test
    public void test_GH_167_deconstruction_first() {
        configureCode("A.ml", """
                let (count, setCount) = React.useState(fun () -> 0)
                let _ = count<caret> 1
                """);

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals(5, elementAtCaret.getTextOffset());
    }

    @Test
    public void test_GH_167_deconstruction_second() {
        configureCode("A.ml", """
                let (count, setCount) = React.useState(fun () -> 0)
                let _ = setCount<caret> 1
                """);

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals(12, elementAtCaret.getTextOffset());
    }

    @Test
    public void test_GH_303() {
        configureCode("B.ml", "type t1 = {bar: string}");
        configureCode("A.ml", "type t = {bar: string} let bar item = item.bar<caret>");

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.t.bar", e.getQualifiedName());
    }

    @Test
    public void test_GH_303_2() {
        configureCode("B.ml", "type t1 = {bar:string}");
        configureCode("A.ml", "type t = {bar: string} let bar<caret> item = item.bar");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.bar", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/358
    @Test
    public void test_GH_358() {
        configureCode("A.ml", """
                let clearPath () = ()
                module Xxx = struct
                  type t = | ClearPath
                  let clearPath () = ()
                end
                let reducer = function | Xxx.ClearPath -> clearPath<caret>()
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.clearPath", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/461
    @Test
    public void test_GH_461_parameter_type() {
        configureCode("A.ml", """
                type store = {x: int}
                let fn (store: store<caret>) = store.x
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.store", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }
}
