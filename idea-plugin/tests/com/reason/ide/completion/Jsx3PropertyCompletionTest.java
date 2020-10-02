package com.reason.ide.completion;

import com.reason.ide.ORBasePlatformTestCase;
import java.util.*;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class Jsx3PropertyCompletionTest extends ORBasePlatformTestCase {

  @NotNull
  @Override
  protected String getTestDataPath() {
    return "testData/com/reason/lang";
  }

  public void test_Rml_shouldDisplayProperties() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("Component.re", "[@react.component] let make = (~name, ~onClose) => <div/>;");
    configureCode("A.re", "let _ = <Component <caret>>");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    assertSize(4, completions);
    assertContainsElements(completions, "key", "ref", "name", "onClose");
  }

  public void test_Ns_shouldDisplayProperties() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("Component.res", "[@react.component] let make = (~name, ~onClose) => <div/>;");
    configureCode("A.res", "let _ = <Component <caret>>");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    assertSize(4, completions);
    assertContainsElements(completions, "key", "ref", "name", "onClose");
  }

  public void test_Rml_houldDisplayPropertiesAfterPropName() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("Component.re", "[@react.component] let make = (~name, ~onClose) => <div/>;");
    configureCode("A.re", "let _ = <Component o<caret> >");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    // from doc: null if the only item was auto-completed
    assertNull(completions);
  }

  public void test_Ns_houldDisplayPropertiesAfterPropName() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("Component.res", "[@react.component] let make = (~name, ~onClose) => <div/>;");
    configureCode("A.res", "let _ = <Component o<caret> >");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    // from doc: null if the only item was auto-completed
    assertNull(completions);
  }
}
