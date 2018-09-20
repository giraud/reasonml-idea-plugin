package com.reason.ide.folding;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class TypeFoldingTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/ide/folding";
    }

    public void testModuleLetCompletion() {
        // NOT WORKING
        // myFixture.testFolding(getTestDataPath() + "/TypeFolding.re");
    }
}