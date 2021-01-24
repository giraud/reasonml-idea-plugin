package com.reason.ide.completion;

import com.reason.ide.*;
import org.jetbrains.annotations.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class Jsx3PropertyCompletionRSTest extends ORBasePlatformTestCase {

  @NotNull
  @Override
  protected String getTestDataPath() {
    return "testData/com/reason/lang";
  }

  public void test_shouldDisplayProperties() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("Component.res", "@react.component let make = (~name, ~onClose) => <div/>;");
    configureCode("A.res", "let _ = <Component <caret> >");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    assertSize(4, completions);
    assertContainsElements(completions, "key", "ref", "name", "onClose");
  }

  public void test_shouldDisplayPropertiesAfterPropName() {
    myFixture.configureByFiles("pervasives.ml");
    configureCode("Component.res", "@react.component let make = (~name, ~onClose) => <div/>;");
    configureCode("A.res", "let _ = <Component o<caret> >");

    myFixture.completeBasic();

    List<String> completions = myFixture.getLookupElementStrings();
    // from doc: null if the only item was auto-completed
    assertNull(completions);
  }
}
