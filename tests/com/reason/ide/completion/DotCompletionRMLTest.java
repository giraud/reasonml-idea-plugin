package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class DotCompletionRMLTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "type t; let y = 2; module B = {}; A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void test_module_override() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "module A = { let y = 2; }; A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "y");
    }

    public void test_before_caret() {
        configureCode("A.re", "type x;");
        configureCode("B.re", "A.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void test_end_of_file() {
        configureCode("A.re", "type x;");
        configureCode("B.re", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    public void test_single_alias() {
        // like ReasonReact.Router
        configureCode("ReasonReactRouter.rei", "type watcherID;");
        configureCode("ReasonReact.rei", "module Router = ReasonReactRouter;");

        configureCode("Dummy.re", "ReasonReact.Router.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("watcherID", elements.get(0));
    }

    public void test_alias_in_file() {
        // like ReasonReact.Router
        configureCode("View.re", "module Detail = { let alias = \"a\"; };");
        configureCode("Dummy.re", "module V = View.Detail; V.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("alias", elements.get(0));
    }

    public void test_uncurried() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "send(. <caret>)"); // should use free completion

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A");
    }

    public void test_let_private() {
        configureCode("A.re", "let x%private = 1;");
        configureCode("B.re", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertEmpty(strings);
    }

    public void test_functor_no_return_type() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) => { let y = 1; };");
        configureCode("B.re", "open A; module Instance = MakeIntf({let x = true});");
        configureCode("C.re", "open B; Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    public void test_functor_with_return_type() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) : Intf => { let y = 1; };");
        configureCode("B.re", "open A; module Instance = MakeIntf({let x = true});");
        configureCode("C.re", "open B; Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    public void test_functor_include() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) => { let y = 1; };");
        configureCode("B.re", "include A.MakeIntf({ let x = true; });");
        configureCode("C.re", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    public void test_functor_include_multiple_choice() {
        configureCode("A.re", "module Make = (I:{}) => { let a = 1; };");
        configureCode("B.re", "module Make = (I:{}) => { let b = 1; }; include Make({});");
        configureCode("C.re", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Make", "b");
    }

    public void test_functor_include_alias() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) => { let y = 1; };");
        configureCode("B.re", "module Instance = A.MakeIntf({let x = true}); include Instance;");
        configureCode("C.re", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Instance", "y");
    }

    public void test_result_with_alias() {
        configureCode("A.re", "module type Result = { let a: int; };");
        configureCode("B.re", "module T = A; module Make = (M:Intf): T.Result => { let b = 3; };");
        configureCode("C.re", "module Instance = B.Make({}); let c = Instance.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }

    public void test_result_with_alias2() {
        configureCode("A.re", "module type Result = { let a: int; };");
        configureCode("B.re", "module Make = (M:Intf): (A.Result with type t := M.t) => {}; module Instance = Make({});");
        configureCode("C.re", "B.Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }

    public void test_variant() {
        configureCode("A.re", "type color = | Black | Red;");
        configureCode("B.re", "A.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(3, elements);
        assertContainsElements(elements, "color", "Black", "Red");
    }
}
