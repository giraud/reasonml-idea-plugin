package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class DotCompletion_RES_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("A.res", "let x = 1");
        configureCode("B.res", """
                type t
                let y = 2
                module B = {}
                A.<caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    @Test
    public void test_module_override() {
        configureCode("A.res", "let x = 1");
        configureCode("B.res", """
                module A = { let y = 2 }
                A.<caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "y");
    }

    @Test
    public void test_before_caret() {
        configureCode("A.res", "type x");
        configureCode("B.res", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    @Test
    public void test_end_of_file() {
        configureCode("A.res", "type x");
        configureCode("B.res", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    @Test
    public void test_single_alias() {
        configureCode("C.resi", "type t");
        configureCode("B.resi", "module B1 = C");
        configureCode("A.res", "B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertEquals("t", strings.getFirst());
    }

    @Test
    public void test_alias_in_file() {
        // like ReasonReact.Router
        configureCode("View.res", "module Detail = { let alias = \"a\" }");
        configureCode("Dummy.res", """
                module V = View.Detail
                V.<caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertEquals("alias", strings.getFirst());
    }

    @Test
    public void test_alias() {
        configureCode("A.res", "module A1 = {}");
        configureCode("B.res", "module B1 = { include A }");
        configureCode("C.res", "module C1 = B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertEquals("A1", strings.getFirst());
    }

    @Test
    public void test_alias_of_alternates() {
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
        configureCode("Aa.res", "let x = 1");
        configureCode("B.res", "send(. <caret>)"); // should use free completion

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "Aa");
    }

    @Test
    public void test_functor_no_return_type() {
        configureCode("A.res", "module type Intf = { let x: bool }\n module MakeIntf = (I:Intf) => { let y = 1 }");
        configureCode("B.res", "open A\n module Instance = MakeIntf({let x = true})");
        configureCode("C.res", "open B\n Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    @Test
    public void test_functor_with_return_type() {
        configureCode("A.res", "module type Intf = { let x: bool }\n module MakeIntf = (I:Intf) : Intf => { let y = 1 }");
        configureCode("B.res", "open A\n module Instance = MakeIntf({let x = true})");
        configureCode("C.res", "open B\n Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    @Test
    public void test_functor_include() {
        configureCode("A.res", "module type Intf = { let x: bool }\n module MakeIntf = (I:Intf) => { let y = 1 }");
        configureCode("B.res", "include A.MakeIntf({ let x = true })");
        configureCode("C.res", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    @Test
    public void test_functor_include_multiple_choice() {
        configureCode("A.res", "module Make = (I:{}) => { let a = 1 }");
        configureCode("B.res", "module Make = (I:{}) => { let b = 1 }\n include Make({})");
        configureCode("C.res", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Make", "b");
    }

    @Test
    public void test_functor_include_alias() {
        configureCode("A.res", "module type Intf = { let x: bool }\n module MakeIntf = (I:Intf) => { let y = 1 }");
        configureCode("B.res", "module Instance = A.MakeIntf({let x = true})\n include Instance");
        configureCode("C.res", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Instance", "y");
    }

    @Test
    public void test_result_with_alias() {
        configureCode("A.res", "module type Result = { let a: int }");
        configureCode("B.res", "module T = A\n module Make = (M:Intf): T.Result => { let b = 3 }");
        configureCode("C.res", "module Instance = B.Make({})\n let c = Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }

    @Test
    public void test_result_with_alias2() {
        configureCode("A.res", "module type Result = { let a: int }");
        configureCode("B.res", "module Make = (M:Intf): (A.Result with type t := M.t) => {}\n module Instance = Make({})");
        configureCode("C.res", "B.Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }

    @Test
    public void test_variant() {
        configureCode("A.res", "type color = | Black | Red");
        configureCode("B.res", "A.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(3, elements);
        assertContainsElements(elements, "color", "Black", "Red");
    }

    @Test
    public void test_parameter() {
        configureCode("A.res", """
                type store = {x: int, y: int}
                let fn = (store: store) => store.<caret>
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "x", "y");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/452
    @Test
    public void test_GH_452_unpacked_module() {
        configureCode("A.res", """
                module type I = {
                  let x: int
                }

                let x = (~p: module(I)) => {
                    module S = unpack(p)
                    S.<caret>
                };
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertContainsElements(elements, "x");
    }
}
