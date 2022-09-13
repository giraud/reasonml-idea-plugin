package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FreeCompletionRESTest extends ORBasePlatformTestCase {
    public void test_pervasives() {
        configureCode("pervasives.mli", "val int_of_string : str -> int");
        configureCode("belt_Array.mli", "val length: t -> int");
        configureCode("belt.ml", "module Array = Belt_Array");

        configureCode("Dummy.res", "let x = <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(5, elements);
        assertContainsElements(elements, "int_of_string", "Belt", "Belt_Array", "Pervasives", "x");
    }

    public void test_deconstruction() {
        configureCode("Dummy.res", "let (first, second) = myVar <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "first", "second");
    }

    public void test_include() {
        myFixture.configureByText("A.res", "let x = 1");
        myFixture.configureByText("B.res", "include A\n<caret>\n");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(2, strings);
        assertSameElements(strings, "x", "A");
    }

    //public void test_include_eof() {
    //    myFixture.configureByText("A.res", "let x = 1");
    //    myFixture.configureByText("B.res", "include A\n let y = 2\n <caret>");
    //
    //    myFixture.complete(CompletionType.BASIC, 1);
    //    List<String> strings = myFixture.getLookupElementStrings();
    //
    //    assertSize(10, strings);
    //    assertSameElements(strings, "exception", "external", "include", "let", "module", "open", "type", "A", "y", "x");
    //}
}
