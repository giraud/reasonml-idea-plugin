package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

import java.util.List;

public class CommentCompletionTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/lang";
    }

    public void testCommentCompletion() {
        myFixture.configureByFiles("pervasives.ml");
        myFixture.configureByText("Comment.re", "/*<caret>*/");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertEmpty(strings);
    }

    public void testCommentStartCompletion() {
        myFixture.configureByFiles("pervasives.ml");
        myFixture.configureByText("Comment.re", "/*<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertEmpty(strings);
    }

}
