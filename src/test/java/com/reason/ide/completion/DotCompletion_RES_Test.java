package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
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
        // like ReasonReact.Router
        configureCode("ReasonReactRouter.resi", "type watcherID");
        configureCode("ReasonReact.resi", "module Router = ReasonReactRouter");

        configureCode("Dummy.res", "ReasonReact.Router.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("watcherID", elements.get(0));
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
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("alias", elements.get(0));
    }

    @Test
    public void test_alias() {
        configureCode("A.res", "module A1 = {}");
        configureCode("B.res", "module B1 = { include A }");
        configureCode("C.res", "module C1 = B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("A1", elements.get(0));
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
