package com.reason.ide.completion;

import com.reason.ide.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class Jsx3PropertyCompletion_RES_Test extends ORBasePlatformTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "src/test/testData/com/reason/lang";
    }

    @Test
    public void test_display_properties_let() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("Component.res", "@react.component let make = (~name, ~onClose=?) => <div/>");
        configureCode("A.res", "let _ = <Component <caret>>");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertSize(4, completions);
        assertContainsElements(completions, "key", "ref", "name", "onClose");
    }

    @Test
    public void test_display_properties_external() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("Component.res", "@react.component external make : (~name:string, ~onClose: unit => unit) = \"Comp\"");
        configureCode("A.res", "let _ = <Component <caret>>");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertContainsElements(completions, "key", "ref", "name", "onClose");
        assertSize(4, completions);
    }

    @Test
    public void test_shouldDisplayPropertiesAfterPropName() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("Component.res", "@react.component let make = (~name, ~onClose) => <div/>");
        configureCode("A.res", "let _ = <Component o<caret> >");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        // from doc: null if the only item was auto-completed
        assertNull(completions);
    }

    @Test
    public void test_shouldDisplayProperties_nested() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("A.res", "module Comp = { @react.component let make = (~name) => <div/> }");
        configureCode("B.res", "@react.component let make = () => <A.Comp <caret> />");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertSize(3, completions);
        assertContainsElements(completions, "key", "ref", "name");
    }

    @Test
    public void test_shouldDisplayProperties_open() {
        myFixture.configureByFiles("pervasives.ml");
        configureCode("A.res", "module Comp = { @react.component let make = (~name) => <div/> }");
        configureCode("B.res", "open A\n @react.component let make = () => <Comp <caret> />\n module Comp = { @react.component let make = (~value) => <div/> }");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertSize(3, completions);
        assertContainsElements(completions, "key", "ref", "name");
    }

    @Test
    public void test_div() {
        myFixture.configureByFiles("ReactDOM.res");
        configureCode("A.res", "let _ = <div <caret>>");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertContainsElements(completions, "key", "ref", "ariaDetails", "className", "onClick");
        assertSize(5, completions);
    }
}
