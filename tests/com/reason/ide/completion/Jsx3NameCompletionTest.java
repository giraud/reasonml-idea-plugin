package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class Jsx3NameCompletionTest extends ORBasePlatformTestCase {

  public void test_outside_components() {
    configureCode("DialogHeader.re", "[@react.component] let make = () => { <div/> };");
    configureCode("DialogFooter.re", "[@react.component] let make = () => { <div/> };");
    configureCode("Dialog.re", "[@react.component] let make = () => <Dia<caret>");

    myFixture.completeBasic();
    List<String> completions = myFixture.getLookupElementStrings();

    assertContainsElements(completions, "DialogHeader", "DialogFooter");
    assertSize(2, completions);
  }

  public void test_dont_display_properties() {
    configureCode("DialogHeader.re", "[@react.component] let make = () => { <div/> };");
    configureCode("Dummy.re", "let _ = <<caret>Dialog");

    myFixture.complete(CompletionType.BASIC, 1);
    List<String> completions = myFixture.getLookupElementStrings();

    assertEquals(1, completions.size());
    assertEquals("DialogHeader", completions.get(0));
  }
}
