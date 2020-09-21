package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.reason.ide.ORBasePlatformTestCase;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class KeywordCompletionTest extends ORBasePlatformTestCase {

  public void testRml_Basic() {
    configureCode("B.re", "<caret>");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> strings = myFixture.getLookupElementStrings();

    assertSameElements(
        strings, "open", "include", "module", "type", "let", "external", "exception");
  }

  public void testOcl_Basic() {
    configureCode("B.ml", "<caret>");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> strings = myFixture.getLookupElementStrings();

    assertSameElements(
        strings, "open", "include", "module", "type", "let", "external", "exception");
  }
}
