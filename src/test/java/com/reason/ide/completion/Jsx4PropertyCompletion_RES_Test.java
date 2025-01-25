package com.reason.ide.completion;

import com.reason.comp.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class Jsx4PropertyCompletion_RES_Test extends ORBasePlatformTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "src/test/testData/com/reason/lang";
    }

    @Test
    public void test_div() {
        myFixture.configureByFiles("jsxDOMU.res");
        myFixture.configureByText(ORConstants.RESCRIPT_CONFIG_FILENAME, toJson("{'name': 'foo', 'jsx': {'version': 4}}"));

        configureCode("A.res", "let _ = <div <caret>>");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertContainsElements(completions, "key", "ref", "ariaCurrent", "className", "dataTestId", "onClick");
        assertSize(6, completions);
    }

    @Test
    public void test_div_used_names() {
        myFixture.configureByFiles("jsxDOMU.res");
        myFixture.configureByText(ORConstants.RESCRIPT_CONFIG_FILENAME, toJson("{'name': 'foo', 'jsx': {'version': 4}}"));

        configureCode("A.res", "let _ = <div ariaCurrent='x' className='x' <caret>>");

        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertContainsElements(completions, "key", "ref", "dataTestId", "onClick");
        assertSize(4, completions);
    }
}
