package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class Jsx3NameCompletionTest extends ORBasePlatformTestCase {

  public void testShouldDisplayComponents() {
    configureCode("DialogHeader.re", "[@react.component] let make = () => { <div/> };");
    configureCode("DialogFooter.re", "[@react.component] let make = () => { <div/> };");
    configureCode("Dummy.re", "let _ = <Dia<caret>");

    myFixture.completeBasic();
    List<String> completions = myFixture.getLookupElementStrings();

    assertSize(2, completions);
    assertContainsElements(completions, "DialogHeader", "DialogFooter");
  }

  public void testShouldNotDisplayProperties() {
    configureCode("DialogHeader.re", "[@react.component] let make = () => { <div/> };");
    configureCode("Dummy.re", "let _ = <<caret>Dialog");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> completions = myFixture.getLookupElementStrings();

    assertEquals(1, completions.size());
    assertEquals("DialogHeader", completions.get(0));
  }
}
