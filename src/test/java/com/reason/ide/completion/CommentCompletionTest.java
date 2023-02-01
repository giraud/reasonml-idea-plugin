package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class CommentCompletionTest extends BasePlatformTestCase {
  @Override
  protected @NotNull String getTestDataPath() {
    return "src/test/testData/com/reason/lang";
  }

  @Test
  public void testCommentCompletion() {
    myFixture.configureByFiles("pervasives.ml");
    myFixture.configureByText("Comment.re", "/*<caret>*/");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> strings = myFixture.getLookupElementStrings();

    assertEmpty(strings);
  }

  @Test
  public void testCommentStartCompletion() {
    myFixture.configureByFiles("pervasives.ml");
    myFixture.configureByText("Comment.re", "/*<caret>");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> strings = myFixture.getLookupElementStrings();

    assertEmpty(strings);
  }
}
