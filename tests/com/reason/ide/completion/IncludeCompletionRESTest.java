package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.testFramework.fixtures.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class IncludeCompletionRESTest extends BasePlatformTestCase {
    public void test_include() {
        myFixture.configureByText("A.res", "let x = 1");
        myFixture.configureByText("B.res", "include A\n<caret>\n");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(2, strings);
        assertSameElements(strings, "x", "A");
    }

    public void test_include_eof() {
        myFixture.configureByText("A.res", "let x = 1");
        myFixture.configureByText("B.res", "include A\nlet y = 2\n<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(10, strings);
        assertSameElements(strings, "exception", "external", "include", "let", "module", "open", "type", "A", "y", "x");
    }
}
