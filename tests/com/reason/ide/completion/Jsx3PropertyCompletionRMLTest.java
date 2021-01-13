package com.reason.ide.completion;

import com.reason.ide.*;
import org.jetbrains.annotations.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class Jsx3PropertyCompletionRMLTest extends ORBasePlatformTestCase {

  @NotNull
  @Override
  protected String getTestDataPath() {
    return "testData/com/reason/lang";
  }

  public void test_shouldDisplayProperties() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("Component.re", "[@react.component] let make = (~name, ~onClose) => <div/>;");
    configureCode("A.re", "let _ = <Component <caret>>");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    assertSize(4, completions);
    assertContainsElements(completions, "key", "ref", "name", "onClose");
  }

  public void test_shouldDisplayPropertiesAfterPropName() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("Component.re", "[@react.component] let make = (~name, ~onClose) => <div/>;");
    configureCode("A.re", "let _ = <Component o<caret> >");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    // from doc: null if the only item was auto-completed
    assertNull(completions);
  }

  public void test_shouldDisplayProperties_nested() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("A.re", "module Comp = { [@react.component] let make = (~name) => <div/>; };");
    configureCode("B.re", "[@react.component] let make = () => <A.Comp <caret> />");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    assertSize(3, completions);
    assertContainsElements(completions, "key", "ref", "name");
  }

  public void test_shouldDisplayProperties_open() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("A.re", "module Comp = { [@react.component] let make = (~name) => <div/>; };");
    configureCode("B.re", "open A; [@react.component] let make = () => <Comp <caret> />; module Comp = { [@react.component] let make = (~value) => <div/>; };");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    assertSize(3, completions);
    assertContainsElements(completions, "key", "ref", "name");
  }

}
