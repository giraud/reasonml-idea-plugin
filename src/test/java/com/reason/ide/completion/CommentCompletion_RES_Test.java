package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.testFramework.fixtures.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@RunWith(JUnit4.class)
public class CommentCompletion_RES_Test extends BasePlatformTestCase {
  @Override
  protected @NotNull String getTestDataPath() {
    return "src/test/testData/com/reason/lang";
  }

  @Test
  public void testCommentCompletion() {
    myFixture.configureByFiles("pervasives.ml");
    myFixture.configureByText("Comment.res", "/*<caret>*/");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> strings = myFixture.getLookupElementStrings();

    assertNullOrEmpty(strings);
  }

  @Test
  public void testCommentStartCompletion() {
    myFixture.configureByFiles("pervasives.ml");
    myFixture.configureByText("Comment.res", "/*<caret>");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> strings = myFixture.getLookupElementStrings();

    assertNullOrEmpty(strings);
  }
}
