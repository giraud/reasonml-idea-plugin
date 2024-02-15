package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
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

        assertContainsElements(elements, "int_of_string", "Belt", "Belt_Array", "Pervasives", "x");
        assertSize(5, elements);
    }

    // TODO: rest of file
}
