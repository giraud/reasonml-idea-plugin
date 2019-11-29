package com.reason.ide.completion;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.files.RmlFileType;

@SuppressWarnings("ConstantConditions")
public class Jsx3PropertyCompletionTest extends LightPlatformCodeInsightFixtureTestCase {

    @NotNull
    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/lang";
    }

    public void testShouldDisplayProperties() {
        myFixture.configureByFiles("pervasives.ml");
        myFixture.configureByText("Component.re", "[@react.component] let make = (~name, ~onClose) => <div/>;");

        myFixture.configureByText(RmlFileType.INSTANCE, "let _ = <Component <caret>>");
        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertSize(4, completions);
        assertContainsElements(completions, "key", "ref", "name", "onClose");
    }

    public void testShouldDisplayPropertiesAfterPropName() {
        myFixture.configureByFiles("pervasives.ml");
        myFixture.configureByText("Component.re", "[@react.component] let make = (~name, ~onClose) => <div/>;");

        myFixture.configureByText(RmlFileType.INSTANCE, "let _ = <Component o<caret> >");
        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        //assertSize(1, completions);
        //assertContainsElements(completionElements, "key", "ref", "_type", "dismissAfter", "onClose");
    }
}
