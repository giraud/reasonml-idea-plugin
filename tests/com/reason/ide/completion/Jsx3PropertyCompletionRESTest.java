package com.reason.ide.completion;

import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class Jsx3PropertyCompletionRESTest extends ORBasePlatformTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/lang";
    }

    public void test_display_properties_let() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("Component.res", "@react.component let make = (~name, ~onClose=?) => <div/>");
        configureCode("A.res", "let _ = <Component <caret>>");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertSize(4, completions);
        assertContainsElements(completions, "key", "ref", "name", "onClose");
    }

    public void test_display_properties_external() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("Component.res", "@react.component external make : (~name:string, ~onClose: unit => unit) = \"Comp\"");
        configureCode("A.res", "let _ = <Component <caret>>");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertSize(4, completions);
        assertContainsElements(completions, "key", "ref", "name", "onClose");
    }

    public void test_shouldDisplayPropertiesAfterPropName() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("Component.res", "@react.component let make = (~name, ~onClose) => <div/>");
        configureCode("A.res", "let _ = <Component o<caret> >");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        // from doc: null if the only item was auto-completed
        assertNull(completions);
    }

    public void test_shouldDisplayProperties_nested() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("A.res", "module Comp = { @react.component let make = (~name) => <div/> }");
        configureCode("B.res", "@react.component let make = () => <A.Comp <caret> />");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertSize(3, completions);
        assertContainsElements(completions, "key", "ref", "name");
    }

    public void test_shouldDisplayProperties_open() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("A.res", "module Comp = { @react.component let make = (~name) => <div/> }");
        configureCode("B.res", "open A\n @react.component let make = () => <Comp <caret> />\n module Comp = { @react.component let make = (~value) => <div/> }");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertSize(3, completions);
        assertContainsElements(completions, "key", "ref", "name");
    }
}
