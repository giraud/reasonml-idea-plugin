package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import com.reason.ide.insight.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("DataFlowIssue")
public class FreeCompletionOCLTest extends ORBasePlatformTestCase {
    @Test
    public void test_pervasives() {
        configureCode("pervasives.mli", "val int_of_string : str -> int");
        configureCode("belt_Array.mli", "val length: t -> int");
        configureCode("belt.ml", "module Array = Belt_Array");

        configureCode("Dummy.ml", "let x = <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "int_of_string", "Belt", "Belt_Array", "Pervasives");
        assertSize(4, elements);
    }

    @Test
    public void test_underscore() {
        configureCode("Dummy.re", "let _ = 1; <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, OclKeywordCompletionContributor.KEYWORDS);
        assertSize(OclKeywordCompletionContributor.KEYWORDS.length, elements);
    }

    @Test
    public void test_deconstruction() {
        configureCode("Dummy.ml", """
                let (first, second) = myVar
                <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "first", "second");
    }

    @Test
    public void test_include() {
        configureCode("Aa.ml", "let x = 1");
        configureCode("B.ml", """
                include Aa
                <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertContainsElements(strings, "x", "Aa");
    }

    @Test
    public void test_include_after() {
        myFixture.configureByText("A.ml", "let x = 1;");
        myFixture.configureByText("B.ml", """
                <caret>
                include A
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertContainsElements(strings, "A");
    }

    @Test
    public void test_include_eof() {
        myFixture.configureByText("A.ml", "let x = 1");
        myFixture.configureByText("B.ml", """
                include A
                let y = 2
                <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "exception", "external", "include", "let", "module", "open", "type", "A", "y", "x");
        assertSize(10, strings);
    }

    @Test
    public void test_include_functor() {
        configureCode("A.ml", """
                module type I  = sig type renderer end
                module type R  = sig
                  type rule
                  val style : unit -> rule array
                end
                                
                module Core = struct
                    let color = "red"
                    module Make(_:I) : R = struct
                      type rule
                      let style () = [||]
                    end
                end
                                
                module Css = struct
                  include Core
                  include Core.Make(struct type renderer end)
                end
                                
                open Css
                                    
                let y = <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "color", "Core", "Css", "I", "Make", "R", "rule", "style", "y"); // <- y because caret is not inside the let binding
    }

    @Test
    public void test_open_include() {
        configureCode("A.ml", "let x = 1");
        configureCode("B.ml", "include A");
        configureCode("C.ml", "include B");
        configureCode("D.ml", """
                open C
                <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertContainsElements(strings, "A", "B", "C", "x");
    }
}
