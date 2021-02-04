package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class IncludeCompletionTest extends BasePlatformTestCase {

  public void testInclude() {
    myFixture.configureByText("A.re", "let x = 1;");
    myFixture.configureByText("B.re", "include A;\n<caret>\n");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> strings = myFixture.getLookupElementStrings();

    assertSize(2, strings);
    assertSameElements(strings, "x", "A");
  }

  public void testIncludeEOF() {
    myFixture.configureByText("A.re", "let x = 1;");
    myFixture.configureByText("B.re", "include A;\nlet y = 2;\n<caret>");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> strings = myFixture.getLookupElementStrings();

    assertSize(10, strings);
    assertSameElements(
        strings,
        "exception",
        "external",
        "include",
        "let",
        "module",
        "open",
        "type",
        "A",
        "y",
        "x");
  }
}
