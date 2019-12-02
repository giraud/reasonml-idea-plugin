package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.reason.ide.ORBasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentCompletionTest extends ORBasePlatformTestCase {
    @NotNull
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
