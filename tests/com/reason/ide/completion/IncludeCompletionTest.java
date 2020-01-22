package com.reason.ide.completion;

import java.util.*;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

@SuppressWarnings("ConstantConditions")
public class IncludeCompletionTest extends BasePlatformTestCase {

    public void testInclude() {
        myFixture.configureByText("A.re", "let x = 1;");
        myFixture.configureByText("B.re", "include A;\n<caret>\n");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertSameElements(strings, "x");
    }

    public void testIncludeEOF() {
        myFixture.configureByText("A.re", "let x = 1;");
        myFixture.configureByText("B.re", "include A;\nlet y = 2;\n<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(9, strings);
        assertSameElements(strings, "y", "x", "exception", "external", "include", "let", "module", "open", "type");
    }
}
