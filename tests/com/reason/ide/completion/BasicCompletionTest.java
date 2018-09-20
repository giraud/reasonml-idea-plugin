package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

import java.util.List;

public class BasicCompletionTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/ide/completion";
    }

    public void testModuleLetCompletion() {
        myFixture.configureByFiles("BasicCompletion_usage.re", "BasicCompletion_definition.re");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertTrue(strings.contains("x"));
        assertEquals(1, strings.size());
    }
}
