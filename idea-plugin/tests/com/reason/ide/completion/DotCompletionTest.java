package com.reason.ide.completion;

import java.util.*;
import com.intellij.codeInsight.completion.CompletionType;
import com.reason.ide.ORBasePlatformTestCase;

@SuppressWarnings("ConstantConditions")
public class DotCompletionTest extends ORBasePlatformTestCase {

    public void testRml_Basic() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "type t; let y = 2; module B = {}; A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void testOcl_Basic() {
        configureCode("A.ml", "let x = 1");
        configureCode("B.ml", "type t let y = 2 module B = struct end A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void testRml_ModuleOverride() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "module A = { let y = 2; }; A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "y");
    }

    public void testOcl_ModuleOverride() {
        configureCode("A.ml", "let x = 1");
        configureCode("B.ml", "module A = struct let y = 2 end A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "y");
    }

    public void testBeforeCaret() {
        configureCode("A.re", "type x;");
        configureCode("B.re", "A.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void testEndOfFile() {
        configureCode("A.re", "type x;");
        configureCode("B.re", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    public void testMultipleAlias() {
        // like Belt
        configureCode("string.mli", "external length : string -> int = \"%string_length\"");
        configureCode("belt_MapString.mli", "type key = string");
        configureCode("belt_Map.mli", "module String = Belt_MapString");
        configureCode("belt.ml", "module Map = Belt_Map");

        configureCode("Dummy.re", "Belt.Map.String.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("key", elements.get(0));
    }

    public void testSingleAlias() {
        // like ReasonReact.Router
        configureCode("ReasonReactRouter.rei", "type watcherID;");
        configureCode("ReasonReact.rei", "module Router = ReasonReactRouter;");

        configureCode("Dummy.re", "ReasonReact.Router.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("watcherID", elements.get(0));
    }

    public void testRml_AliasInFile() {
        // like ReasonReact.Router
        configureCode("View.re", "module Detail = { let alias = \"a\"; };");
        configureCode("Dummy.re", "module V = View.Detail; V.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("alias", elements.get(0));
    }

    public void testNoPervasives() {
        configureCode("pervasives.mli", "val int_of_string : str -> int");
        configureCode("belt_Array.mli", "val length: t -> int");
        configureCode("belt.ml", "module Array = Belt_Array");

        configureCode("Dummy.re", "Belt.Array.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("length", elements.get(0));
    }

    public void testRml_Uncurried() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "send(. <caret>)"); // should use free completion

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A");
    }

    public void testRml_letprivate() {
        configureCode("A.re", "let x%private = 1;");
        configureCode("B.re", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertEmpty(strings);
    }

    public void testRml_functorNoReturnType() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) => { let y = 1; };");
        configureCode("B.re", "open A; module Instance = MakeIntf({let x = true});");
        configureCode("C.re", "open B; Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    public void testOcl_functorNoReturnType() {
        configureCode("A.ml", "module type Intf  = sig val x : bool end\n module MakeOcl(I:Intf) = struct let y = 1 end");
        configureCode("B.ml", "open A\n module Instance = MakeOcl(struct let x = true end)");
        configureCode("C.ml", "open B let _ = Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    public void testRml_functorWithReturnType() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) : Intf => { let y = 1; };");
        configureCode("B.re", "open A; module Instance = MakeIntf({let x = true});");
        configureCode("C.re", "open B; Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    public void testOcl_functorWithReturnType() {
        configureCode("A.ml", "module type Intf  = sig val x : bool end\n module MakeOcl(I:Intf) : Intf = struct let y = 1 end");
        configureCode("B.ml", "open A\n module Instance = MakeOcl(struct let x = true end)");
        configureCode("C.ml", "open B let _ = Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    public void testRml_functorInclude() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) => { let y = 1; };");
        configureCode("B.re", "include A.MakeIntf({let x = true});");
        configureCode("C.re", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    public void testOcl_functorInclude() {
        configureCode("A.ml", "module type Intf  = sig val x : bool end\n ");
        configureCode("B.ml", "open A\n module MakeOcl(I:Intf) : Intf = struct let y = 1 end\n include MakeOcl(struct let x = true end)");
        configureCode("C.ml", "let _ = B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "MakeOcl", "x");
    }

    public void test_functorIncludeMultipleChoice() {
        configureCode("A.re", "module Make = (I:{}) => { let a = 1; };");
        configureCode("B.re", "module Make = (I:{}) => { let b = 1; }; include Make({});");
        configureCode("C.re", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Make", "b");
    }

    public void testRml_functorIncludeAlias() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) => { let y = 1; };");
        configureCode("B.re", "module Instance = A.MakeIntf({let x = true}); include Instance;");
        configureCode("C.re", "B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "Instance", "y");
    }

    public void testRml_ResultWithAlias() {
        configureCode("A.re", "module type Result = { let a: int; };");
        configureCode("B.re", "module T = A; module Make = (M:Intf): T.Result => { let b = 3; };");
        configureCode("C.re", "module Instance = B.Make({}); let c = Instance.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }

    public void testRml_ResultWithAlias2() {
        configureCode("A.re", "module type Result = { let a: int; };");
        configureCode("B.re", "module Make = (M:Intf): (A.Result with type t := M.t) => {}; module Instance = Make({});");
        configureCode("C.re", "B.Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "a");
    }
}
