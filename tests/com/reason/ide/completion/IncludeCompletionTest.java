package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

import java.util.List;

public class IncludeCompletionTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testInclude() {
        myFixture.configureByText("A.re", "let x = 1;");
        myFixture.configureByText("B.re", "include A;\n<caret>\n");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(2, strings);
        assertSameElements(strings, "A", "x");
    }

    public void testIncludeEOF() {
        myFixture.configureByText("A.re", "let x = 1;");
        myFixture.configureByText("B.re", "include A;\nlet y = 2;\n<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(3, strings);
        assertSameElements(strings, "A", "y", "x");
    }

}
