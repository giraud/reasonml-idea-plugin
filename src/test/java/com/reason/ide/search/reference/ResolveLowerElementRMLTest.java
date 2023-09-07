package com.reason.ide.search.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class ResolveLowerElementRMLTest extends ORBasePlatformTestCase {
    @Test
    public void test_let_basic() {
        configureCode("A.re", "let x = 1; let z = x<caret> + 1;");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.x", e.getQualifiedName());
    }

    @Test
    public void test_call_function_with_module() {
        configureCode("A.re", "let fn=()=>1;");
        configureCode("B.re", "let x=2;");
        configureCode("C.re", "A.fn(); B.x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.x", e.getQualifiedName());
    }

    @Test
    public void test_let_in_module_binding() {
        configureCode("A.re", "let foo = 2; module X = { let foo = 1; let z = foo<caret>; };");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.X.foo", e.getQualifiedName());
    }

    @Test
    public void test_let_inner_scope() {
        configureCode("A.re", "let x = 1; let a = { let x = 2; x<caret> + 10 };");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.a.x", e.getQualifiedName());
    }

    @Test
    public void test_inner_scope_in_function() {
        configureCode("A.re", "let x = 1; let fn = { let x = 2; fn1(x<caret>); };");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.fn.x", e.getQualifiedName());
    }

    @Test
    public void test_inner_scope_in_impl() {
        configureCode("A.rei", "let x:int;");
        configureCode("A.re", "let x = 1; let fn = { let foo = 2; fn1(foo<caret>); };");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.fn.foo", e.getQualifiedName());
        assertEquals("A.re", e.getContainingFile().getName());
    }

    @Test
    public void test_let_local_module_alias() {
        configureCode("A.rei", "let x:int;");
        configureCode("B.re", "let x = 1; module X = A; X.x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.x", e.getQualifiedName());
    }

    @Test
    public void test_alias_path() {
        configureCode("A.re", "module W = { module X = { module Y = { module Z = { let z = 1; }; }; }; };");
        configureCode("B.re", "module C = A.W.X; module D = C.Y.Z; D.z<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.W.X.Y.Z.z", e.getQualifiedName());
    }

    @Test
    public void test_alias_x() {
        configureCode("A.re", "module Mode = { type t; };");
        configureCode("B.re", "module B1 = { module Mode = A.Mode; };");
        configureCode("C.re", "B.B1.Mode.t<caret>");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.Mode.t", e.getQualifiedName());
    }

    @Test
    public void test_open() {
        configureCode("B.re", "let x = 1;");
        configureCode("A.re", "let x = 2; open B; x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.x", e.getQualifiedName());
    }

    @Test
    public void test_alias_open() {
        configureCode("B.re", "let x = 1;");
        configureCode("A.re", "let x = 2; module C = B; open C; x<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.x", e.getQualifiedName());
    }

    @Test
    public void test_let_local_open_parens() {
        configureCode("A.re", "module A1 = { let a = 1; };");
        configureCode("B.re", "let a = 2; let b = A.(A1.a<caret>);");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.A1.a", e.getQualifiedName());
    }

    @Test
    public void test_let_local_open_parens_2() {
        configureCode("A.re", "module A1 = { let a = 3; };");
        configureCode("B.re", "let a = A.A1.(a<caret>);");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.A1.a", e.getQualifiedName());
    }

    @Test
    public void test_type() {
        configureCode("A.re", "type t; type t' = t<caret>;");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.t", e.getQualifiedName());
        assertTrue(e.isAbstract());
    }

    @Test
    public void test_type_with_path() {
        configureCode("A.re", "type t;");
        configureCode("B.re", "type t = A.t<caret>;");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.t", e.getQualifiedName());
    }

    @Test
    public void test_type_with_path_2() {
        configureCode("A.re", "type t; type y = X.Y.t<caret>");

        assertThrows(AssertionError.class, "element not found in file A.re", () -> {
            @SuppressWarnings("unused") PsiElement e = myFixture.getElementAtCaret();
        });
    }

    @Test
    public void test_external() {
        configureCode("A.re", "external e : string -> int = \"name\"; let x = e<caret>");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.e", e.getQualifiedName());
    }

    @Test
    public void test_record_field() {
        configureCode("A.re", """
                type t = { f1: bool, f2: int };
                let x  = { f1: true, f2<caret>: 421 };
                """);

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.t.f2", e.getQualifiedName());
    }

    @Test
    public void test_function() {
        configureCode("A.re", "module B = { let bb = 1; }; module C = { let cc = x => x; }; let z = C.cc(B.bb<caret>);");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.B.bb", e.getQualifiedName());
    }

    @Test
    public void test_function_open() {
        configureCode("B.re", "module C = { let make = x => x; let convert = x => x; }");
        configureCode("A.re", "open B; C.make([| C.convert<caret> |]);");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.C.convert", e.getQualifiedName());
    }

    @Test
    public void test_param_parenLess() {
        configureCode("A.re", "let add10 = x => x<caret> + 10;");

        RPsiParameterDeclaration e = (RPsiParameterDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.add10[x]", e.getQualifiedName());
    }

    @Test
    public void test_local_open_parens() {
        configureCode("A.re", "module A1 = { external a : int = \"\"; };");
        configureCode("B.re", "let b = A.(A1.a<caret>);");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.A1.a", e.getQualifiedName());
    }

    @Test
    public void test_local_open_parens_2() {
        configureCode("A.re", "module A1 = { external a : int = \"\"; };");
        configureCode("B.re", "let a = A.A1.(a<caret>);");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.A1.a", e.getQualifiedName());
    }

    @Test
    public void test_local_open_parens_3() {
        configureCode("A.re", "module A1 = { type t = | Variant; let toString = x => x; };");
        configureCode("B.re", "A.A1.(Variant->toString<caret>);");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.A1.toString", e.getQualifiedName());
    }

    @Test
    public void test_include() {
        configureCode("A.re", "module B = { type t; }; include B; type x = t<caret>;");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.B.t", e.getQualifiedName());
    }

    @Test
    public void test_include_alias() {
        configureCode("A.re", "module B = { type t; }; module C = B; include C; type x = t<caret>;");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.B.t", e.getQualifiedName());
    }

    @Test
    public void test_include_2() {
        configureCode("Css_AtomicTypes.rei", "module Visibility: { type t = [ | `visible | `hidden | `collapse]; };");
        configureCode("Css_Legacy_Core.re", "module Types = Css_AtomicTypes;");
        configureCode("Css.re", "include Css_Legacy_Core;");
        configureCode("A.re", "type layoutRule; let visibility: [< Css.Types.Length.t | Css.Types.Visibility.t<caret> ] => layoutRule;");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Visibility.t", e.getQualifiedName());
    }

    @Test
    public void test_include_qualified() {
        configureCode("A.re", "module B = { module C = { type t; }; }; module D = B; include D.C;");
        configureCode("C.re", "type t = A.t<caret>;");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.B.C.t", e.getQualifiedName());
    }

    @Test
    public void test_module_signature() {
        configureCode("A.re", "module B: { type t; let toString: t => string; }; module C: { type t; let toString: t<caret> => string; };");

        RPsiType e = (RPsiType) myFixture.getElementAtCaret();
        assertEquals("A.C.t", e.getQualifiedName());
    }

    @Test
    public void test_let_Local_open_pipe_first() {
        configureCode("A.re", "module A1 = { let add = x => x + 3; };");
        configureCode("B.re", "let x = A.A1.(x->add<caret>);");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.A1.add", e.getQualifiedName());
    }

    @Test
    public void test_external_local_open_pipe_first() {
        configureCode("A.re", "module A1 = { external add : int => int = \"\"; };");
        configureCode("B.re", "let x = A.A1.(x->add<caret>);");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("A.A1.add", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first() {
        configureCode("Css.mli", "val px: int => string;");
        configureCode("A.re", "Dimensions.spacing.small->Css.px<caret>");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Css.px", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first_open() {
        configureCode("Css.mli", "val px: int => string;");
        configureCode("A.re", "let make = () => { open Css; Dimensions.Spacing.small->px<caret>; }");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Css.px", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first_open_2() {
        configureCode("Core.re", "module Async = { let get = x => x; };");
        configureCode("A.re", "open Core.Async; request->get<caret>(\"windows/settings\")");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Core.Async.get", e.getQualifiedName());
    }

    @Test
    public void test_pipe_first_open_with_path() {
        configureCode("Css.mli", "module Rule = { val px: int => string; };");
        configureCode("A.re", "let make = () => { open Css; Dimensions.spacing.small->Rule.px<caret>; }");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Css.Rule.px", e.getQualifiedName());
    }

    @Test
    public void test_multiple_module() {
        configureCode("Command.re", "module Settings = { module Action = { let convert = x => x; }; };");
        configureCode("A.re", "module C = Y; open Command; Settings.Action.convert<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Command.Settings.Action.convert", e.getQualifiedName());
    }

    @Test
    public void test_variant_constructor() {
        configureCode("B.re", "let convert = x => x;");
        configureCode("A.re", "X.Variant(B.convert<caret>())");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.convert", e.getQualifiedName());
    }

    @Test
    public void test_variant_constructor_tuple() {
        configureCode("B.re", "type t('a) = | Variant('a, 'b);");
        configureCode("A.re", "let x = 1; B.Variant(X.Y, x<caret>)");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.x", e.getQualifiedName());
    }

    @Test
    public void test_open_include() {
        configureCode("Css_Core.re", "let fontStyle = x => x;");
        configureCode("Css.re", "include Css_Core;");
        configureCode("A.re", "open Css; fontStyle<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Css_Core.fontStyle", e.getQualifiedName());
    }

    @Test
    public void test_open_include_deep() {
        configureCode("Css_Rule.re", "let fontStyle = x => x;");
        configureCode("Css_Core.re", "module Rules = { include Css_Rule; }");
        configureCode("Css.re", "include Css_Core;");
        configureCode("A.re", "open Css.Rules; fontStyle<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("Css_Rule.fontStyle", e.getQualifiedName());
    }

    @Test
    public void test_resolution_1() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString;");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.re", "module Option = Belt_Option; module Map = Belt_Map;");
        configureCode("A.re", "let x = (dict, locale) => locale->Belt.Option.flatMap<caret>(dict->Belt.Map.String.get);");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Belt_Option.flatMap", e.getQualifiedName());
    }

    @Test
    public void test_resolution_2() {
        configureCode("Belt_MapString.mli", "val get: 'v t -> key -> 'v option");
        configureCode("Belt_Map.ml", "module String = Belt_MapString;");
        configureCode("Belt_Option.mli", "val flatMap : 'a option -> ('a -> 'b option) -> 'b option");
        configureCode("Belt.re", "module Option = Belt_Option; module Map = Belt_Map;");
        configureCode("A.re", "let x = (dict, locale) => locale->Belt.Option.flatMap(dict->Belt.Map.String.get<caret>);");

        RPsiVal e = (RPsiVal) myFixture.getElementAtCaret();
        assertEquals("Belt_MapString.get", e.getQualifiedName());
    }

    @Test
    public void test_functor_body() {
        configureCode("A.re", "module Make = (M:I) => { let a = 3; };");
        configureCode("B.re", "module Instance = A.Make({}); let b = Instance.a<caret>;");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Make.a", e.getQualifiedName());
    }

    @Test
    public void test_file_include_functor() {
        configureCode("A.re", "module Make = (M:I) => { let a = 3; }; include Make({})");
        configureCode("B.re", "let b = A.a<caret>;");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Make.a", e.getQualifiedName());
    }

    @Test
    public void test_functor_result_with_alias() {
        configureCode("A.re", "module type Result = { let a: int; };");
        configureCode("B.re", "module T = A; module Make = (M:Intf): T.Result => { let b = 3; };");
        configureCode("C.re", "module Instance = B.Make({}); let c = Instance.a<caret>;");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Result.a", e.getQualifiedName());
    }

    @Test
    public void test_path_functor() {
        configureCode("pervasives.mli", "external compare : 'a -> 'a -> int = \"%compare\"");
        configureCode("A.re", "module B = X.Functor({ let cmp = Pervasives.compare<caret>; })");

        RPsiExternal e = (RPsiExternal) myFixture.getElementAtCaret();
        assertEquals("Pervasives.compare", e.getQualifiedName());
    }

    @Test
    public void test_global_local() {
        configureCode("Styles.re", "");
        configureCode("B.re", "");
        configureCode("A.re", """
                open B;
                                
                module Styles = {
                  let x = 1;
                };
                                
                let x = Styles.x<caret>;
                """);

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.Styles.x", e.getQualifiedName());
    }

    //region record
/* TODO
    @Test
    public void test_record_type() {
        configureCode("A.re", "type t = { f1: bool, f2: int }; let x = { f1: true, f2<caret>: 421 };");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.t.f2", e.getQualifiedName());
    }
*/

    @Test
    public void test_record() {
        configureCode("B.re", "let b = { a: 1, b: 2 }; b<caret>");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("B.b", e.getQualifiedName());
    }

    @Test
    public void test_record_l1() {
        configureCode("B.re", "let b = { a: 1, b: 2 }; b.b<caret>");

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("B.b.b", e.getQualifiedName());
    }

    @Test
    public void test_record_l3() {
        configureCode("A.re", "let a = { b: { c: { d: 1 } } }; a.b.c.d<caret>");

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.a.b.c.d", e.getQualifiedName());
    }
    //endregion

    //region object
    @Test
    public void test_object_l1() {
        configureCode("A.re", "let a = { \"b\": 1, \"c\": 2 }; a##b<caret>");

        RPsiObjectField e = (RPsiObjectField) myFixture.getElementAtCaret();
        assertEquals("A.a.b", e.getQualifiedName());
    }

    @Test
    public void test_object_l3() {
        configureCode("A.re", "let a = { \"b\": { \"c\": { \"d\": 1 } } }; a##b##c##d<caret>");

        RPsiObjectField e = (RPsiObjectField) myFixture.getElementAtCaret();
        assertEquals("A.a.b.c.d", e.getQualifiedName());
    }

    @Test
    public void test_deep_open() {
        configureCode("A.re", "let oo = {\"first\": {\"deep\": true}, \"deep\": {\"other\": {\"asd\": 1} } }");
        configureCode("B.re", "open A; oo##deep##other<caret>");

        RPsiObjectField e = (RPsiObjectField) myFixture.getElementAtCaret();
        assertEquals("A.oo.deep.other", e.getQualifiedName());
    }
    //endregion

    @Test
    public void test_GH_167_deconstruction_first() {
        configureCode("A.re", "let (count, setCount) = React.useState(() => 0); count<caret>(1);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals(5, elementAtCaret.getTextOffset());
    }

    @Test
    public void test_GH_167_deconstruction_second() {
        configureCode("A.re", "let (count, setCount) = React.useState(() => 0); setCount<caret>(1);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals(12, elementAtCaret.getTextOffset());
    }

    @Test
    public void test_GH_303() {
        configureCode("B.re", "type t1 = {bar: string};");
        configureCode("A.re", "type t = {bar: string}; let bar = item => item.bar<caret>;");

        RPsiRecordField e = (RPsiRecordField) myFixture.getElementAtCaret();
        assertEquals("A.t.bar", e.getQualifiedName());
    }

    @Test
    public void test_GH_303_2() {
        configureCode("B.re", "type t1 = {bar:string};");
        configureCode("A.re", "type t = {bar: string}; let bar<caret> = item => item.bar;");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.bar", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/358
    @Test
    public void test_GH_358() {
        configureCode("A.re", "let clearPath = () => ();\n " +
                "module Xxx = { type t = | ClearPath; let clearPath = () => (); };\n " +
                "let reducer = fun | Xxx.ClearPath => clearPath<caret>();");

        RPsiLet e = (RPsiLet) myFixture.getElementAtCaret();
        assertEquals("A.clearPath", e.getQualifiedName());
    }
}
