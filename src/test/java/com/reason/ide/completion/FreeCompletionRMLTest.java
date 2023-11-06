package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.comp.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class FreeCompletionRMLTest extends ORBasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/ns";
    }

    @Test
    public void test_pervasives() {
        configureCode("pervasives.mli", "val int_of_string : str -> int");
        configureCode("belt_Array.mli", "val length: t -> int");
        configureCode("belt.ml", "module Array = Belt_Array");

        configureCode("Dummy.re", "let x = <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "int_of_string", "Belt", "Belt_Array", "Pervasives", "x");
        assertSize(5, elements);
    }

    @Test
    public void test_deconstruction() {
        configureCode("Dummy.re", "let (first, second) = myVar; <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "first", "second");
    }

    @Test
    public void test_let_private_from_outside() {
        configureCode("A.re", "let x%private = 1;");
        configureCode("B.re", "open A; <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertDoesntContain(elements, "x");
    }

    @Test
    public void test_let_private_from_inside() {
        configureCode("A.re", "let x%private = 1; <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "x");
    }

    @Test
    public void test_include() {
        myFixture.configureByText("A.re", "let x = 1;");
        myFixture.configureByText("B.re", "include A;\n<caret>\n");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x", "A");
        assertSize(2, strings);
    }

    @Test
    public void test_include_eof() {
        myFixture.configureByText("A.re", "let x = 1;");
        myFixture.configureByText("B.re", "include A;\nlet y = 2;\n<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "exception", "external", "include", "let", "module", "open", "type", "A", "y", "x");
        assertSize(10, strings);
    }

    @Test
    public void test_namespace() {
        myFixture.configureByFile(ORConstants.BS_CONFIG_FILENAME);
        configureCode("A.re", "module A1 = {};");
        configureCode("B.re", "<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "exception", "external", "include", "let", "module", "open", "type", "MyNamespace");
        assertSize(8, strings);
    }
}
