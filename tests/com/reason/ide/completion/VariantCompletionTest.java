package com.reason.ide.completion;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.files.RmlFileType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class VariantCompletionTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @NotNull
    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/ide/completion";
    }

    public void testVariant() {
        myFixture.configureByFiles("VariantCompletion.re");
        myFixture.configureByText(RmlFileType.INSTANCE, "VariantCompletion.<caret>");

        myFixture.completeBasic();

        List<String> completionElements = myFixture.getLookupElementStrings();
        assertSize(3, completionElements);
        assertContainsElements(completionElements, "Black", "color", "Red");
    }

}
