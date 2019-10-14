package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

public class DotCompletionTest extends BasePlatformTestCase {

    public void testModuleLetCompletion() {
        myFixture.configureByText("A.re", "let x = 1;");
        myFixture.configureByText("B.re", "A.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void testBeforeCaret() {
        myFixture.configureByText("A.re", "type x;");
        myFixture.configureByText("B.re", "A.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void testEndOfFile() {
        myFixture.configureByText("A.re", "type x;");
        myFixture.configureByText("B.re", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }
}
