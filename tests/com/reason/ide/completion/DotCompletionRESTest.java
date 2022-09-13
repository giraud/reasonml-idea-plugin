package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class DotCompletionRESTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("A.res", "let x = 1");
        configureCode("B.res", "type t\n let y = 2\n module B = {}\n A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void test_module_override() {
        configureCode("A.res", "let x = 1");
        configureCode("B.res", "module A = { let y = 2 }\n A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "y");
    }

    public void test_before_caret() {
        configureCode("A.res", "type x");
        configureCode("B.res", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void test_end_of_file() {
        configureCode("A.res", "type x");
        configureCode("B.res", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

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

    public void test_alias_in_file() {
        // like ReasonReact.Router
        configureCode("View.res", "module Detail = { let alias = \"a\" }");
        configureCode("Dummy.res", "module V = View.Detail\n V.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("alias", elements.get(0));
    }

    public void test_uncurried() {
        configureCode("A.res", "let x = 1");
        configureCode("B.res", "send(. <caret>)"); // should use free completion

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A");
    }

    public void test_functor_no_return_type() {
        configureCode("A.res", "module type Intf = { let x: bool }\n module MakeIntf = (I:Intf) => { let y = 1 }");
        configureCode("B.res", "open A\n module Instance = MakeIntf({let x = true})");
        configureCode("C.res", "open B\n Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    public void test_functor_with_return_type() {
        configureCode("A.res", "module type Intf = { let x: bool }\n module MakeIntf = (I:Intf) : Intf => { let y = 1 }");
        configureCode("B.res", "open A\n module Instance = MakeIntf({let x = true})");
        configureCode("C.res", "open B\n Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    public void test_functor_include() {
        configureCode("A.res", "module type Intf = { let x: bool }\n module MakeIntf = (I:Intf) => { let y = 1 }");
        configureCode("B.res", "include A.MakeIntf({ let x = true })");
        configureCode("C.res", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    public void test_functor_include_multiple_choice() {
        configureCode("A.res", "module Make = (I:{}) => { let a = 1 }");
        configureCode("B.res", "module Make = (I:{}) => { let b = 1 }\n include Make({})");
        configureCode("C.res", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Make", "b");
    }

    public void test_functor_include_alias() {
        configureCode("A.res", "module type Intf = { let x: bool }\n module MakeIntf = (I:Intf) => { let y = 1 }");
        configureCode("B.res", "module Instance = A.MakeIntf({let x = true})\n include Instance");
        configureCode("C.res", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Instance", "y");
    }

    public void test_result_with_alias() {
        configureCode("A.res", "module type Result = { let a: int }");
        configureCode("B.res", "module T = A\n module Make = (M:Intf): T.Result => { let b = 3 }");
        configureCode("C.res", "module Instance = B.Make({})\n let c = Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }

    public void test_result_with_alias2() {
        configureCode("A.res", "module type Result = { let a: int }");
        configureCode("B.res", "module Make = (M:Intf): (A.Result with type t := M.t) => {}\n module Instance = Make({})");
        configureCode("C.res", "B.Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }

    public void test_variant() {
        configureCode("A.res", "type color = | Black | Red");
        configureCode("B.res", "A.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(3, elements);
        assertContainsElements(elements, "color", "Black", "Red");
    }
}
