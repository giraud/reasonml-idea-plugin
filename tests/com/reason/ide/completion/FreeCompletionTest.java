package com.reason.ide.completion;

import java.util.*;
import com.intellij.codeInsight.completion.CompletionType;
import com.reason.ide.ORBasePlatformTestCase;

@SuppressWarnings("ConstantConditions")
public class FreeCompletionTest extends ORBasePlatformTestCase {

    public void testPervasives() {
        configureCode("pervasives.mli", "val int_of_string : str -> int");
        configureCode("belt_Array.mli", "val length: t -> int");
        configureCode("belt.ml", "module Array = Belt_Array");

        configureCode("Dummy.re", "let x = <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(4, elements);
        assertContainsElements(elements, "int_of_string", "Belt", "Belt_Array", "x");
    }
}
