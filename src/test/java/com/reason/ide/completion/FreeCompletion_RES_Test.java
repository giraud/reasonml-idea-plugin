package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import com.reason.ide.insight.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FreeCompletion_RES_Test extends ORBasePlatformTestCase {
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
    public void test_underscore() {
        configureCode("Dummy.res", """
                let _ = 1
                <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, ResKeywordCompletionContributor.KEYWORDS);
        assertSize(ResKeywordCompletionContributor.KEYWORDS.length, elements);
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

        assertSameElements(strings, "color", "Core", "Css", "I", "Make", "R", "style", "y"); // <- y because caret is not inside the let binding
    }

    @Test
    public void test_open_include() {
        configureCode("A.res", "let x = 1");
        configureCode("B.res", "include A");
        configureCode("C.res", "include B");
        configureCode("D.res", """
                open C
                <caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = getLookupStrings();

        assertSameElements(strings, "A", "B", "C", "x");
    }

    @Test
    public void test_parameters() {
        configureCode("A.res", "let fn = (newValue, newUnit) => { n<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = getLookupStrings();

        assertSameElements(strings, "newValue", "newUnit");
    }

    @Test
    public void test_named_parameters() {
        configureCode("A.res", "let fn = (~newValue, ~newUnit:option<string>, ~newOther=?) => { n<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = getLookupStrings();

        assertSameElements(strings, "newValue", "newUnit", "newOther");
    }

    @Test
    public void test_GH_246() {
        configureCode("A.res", """
                let fn = (newValue, newUnit) => {
                    setSomething(_ => {value: n<caret>
                }
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = getLookupStrings();

        assertSameElements(strings, "newValue", "newUnit");
    }

    private List<String> getLookupStrings() {
        List<String> elements = myFixture.getLookupElementStrings();
        elements.removeAll(List.of(ResKeywordCompletionContributor.KEYWORDS));
        return elements;
    }
}
