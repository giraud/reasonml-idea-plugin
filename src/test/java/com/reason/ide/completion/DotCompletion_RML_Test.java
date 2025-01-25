package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class DotCompletion_RML_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "type t; let y = 2; module B = {}; A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    @Test
    public void test_module_override() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "module A = { let y = 2; }; A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "y");
    }

    @Test
    public void test_before_caret() {
        configureCode("A.re", "type x;");
        configureCode("B.re", "A.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    @Test
    public void test_end_of_file() {
        configureCode("A.re", "type x;");
        configureCode("B.re", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    @Test
    public void test_single_alias() {
        configureCode("C.rei", "type t;");
        configureCode("B.rei", "module B1 = C;");
        configureCode("A.re", "B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertEquals("t", strings.getFirst());
    }

    @Test
    public void test_alias_in_file() {
        // like ReasonReact.Router
        configureCode("View.re", "module Detail = { let alias = \"a\"; };");
        configureCode("Dummy.re", "module V = View.Detail; V.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertEquals("alias", strings.getFirst());
    }

    @Test
    public void test_alias() {
        configureCode("A.re", "module A1 = {};");
        configureCode("B.re", "module B1 = { include A; };");
        configureCode("C.re", "module C1 = B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("A1", elements.getFirst());
    }

    @Test
    public void test_alias_and_alternates() {
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
                let _ = C1.<caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "A1", "B5");
        assertSize(2, elements);
    }

    @Test
    public void test_uncurried() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "send(. <caret>)"); // should use free completion

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A");
    }

    @Test
    public void test_let_private() {
        configureCode("A.re", "let x%private = 1;");
        configureCode("B.re", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertEmpty(strings);
    }

    @Test
    public void test_functor_no_return_type() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) => { let y = 1; };");
        configureCode("B.re", "open A; module Instance = MakeIntf({let x = true});");
        configureCode("C.re", "open B; Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    @Test
    public void test_functor_with_return_type() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module type Sig = { let y: int}; module MakeIntf = (I:Intf) : Sig => { let y = 1; };");
        configureCode("B.re", "open A; module Instance = MakeIntf({let x = true});");
        configureCode("C.re", "open B; Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    @Test
    public void test_functor_include() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) => { let y = 1; };");
        configureCode("B.re", "include A.MakeIntf({ let x = true; });");
        configureCode("C.re", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    @Test
    public void test_functor_include_multiple_choice() {
        configureCode("A.re", "module Make = (I:{}) => { let a = 1; };");
        configureCode("B.re", "module Make = (I:{}) => { let b = 1; }; include Make({});");
        configureCode("C.re", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Make", "b");
    }

    @Test
    public void test_functor_include_alias() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) => { let y = 1; };");
        configureCode("B.re", "module Instance = A.MakeIntf({let x = true}); include Instance;");
        configureCode("C.re", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Instance", "y");
    }

    @Test
    public void test_result_with_alias() {
        configureCode("A.re", "module type Result = { let a: int; };");
        configureCode("B.re", "module T = A; module Make = (M:Intf): T.Result => { let b = 3; };");
        configureCode("C.re", "module Instance = B.Make({}); let c = Instance.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }

    @Test
    public void test_result_with_alias2() {
        configureCode("A.re", "module type Result = { let a: int; };");
        configureCode("B.re", "module Make = (M:Intf): (A.Result with type t := M.t) => {}; module Instance = Make({});");
        configureCode("C.re", "B.Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }

    @Test
    public void test_variant() {
        configureCode("A.re", "type color = | Black | Red;");
        configureCode("B.re", "A.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(3, elements);
        assertContainsElements(elements, "color", "Black", "Red");
    }

    @Test
    public void test_parameter() {
        configureCode("A.re", """
                type store = {x: int, y: int};
                let fn = (store: store) => store.<caret>
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "x", "y");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/452
    @Test
    public void test_GH_452_unpacked_module() {
        configureCode("A.re", """
                module type I = {
                  let x: int;
                };

                let x = (~p: (module I)) => {
                    module S = (val p);
                    S.<caret>
                };
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertContainsElements(elements, "x");
    }
}
