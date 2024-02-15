package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class FreeCompletionRESTest extends ORBasePlatformTestCase {
    @Test
    public void test_pervasives() {
        configureCode("pervasives.mli", "val int_of_string : str -> int");
        configureCode("belt_Array.mli", "val length: t -> int");
        configureCode("belt.ml", "module Array = Belt_Array");

        configureCode("Dummy.res", "let x = <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "int_of_string", "Belt", "Belt_Array", "Pervasives");
        assertSize(4, elements);
    }

    @Test
    public void test_deconstruction() {
        configureCode("Dummy.res", """
                let (first, second) = myVar
                <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "first", "second");
    }

    @Test
    public void test_include() {
        configureCode("Aa.res", "let x = 1");
        configureCode("B.res", """
                include Aa
                <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertContainsElements(strings, "x", "Aa");
    }

    @Test
    public void test_include_after() {
        myFixture.configureByText("A.res", "let x = 1");
        myFixture.configureByText("B.res", """
                <caret>
                include A
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertContainsElements(strings, "A");
    }

    @Test
    public void test_include_eof() {
        myFixture.configureByText("A.res", "let x = 1");
        myFixture.configureByText("B.res", """
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
        configureCode("A.res", """
                module type I = { type renderer }
                module type R = {
                  type rule
                  let style: unit => array<rule>
                }
                                    
                module Core = {
                  let color = "red"
                  module Make = (I): R => {
                    type rule
                    let style = () => []
                  }
                }
                                    
                module Css = {
                  include Core
                  include Core.Make({ type renderer })
                };
                                    
                open Css
                                    
                let y = <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "color", "Core", "Css", "I", "Make", "R", "rule", "style", "y"); // <- y because caret is not inside the let binding
    }
}
