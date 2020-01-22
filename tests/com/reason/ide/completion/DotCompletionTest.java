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
}
