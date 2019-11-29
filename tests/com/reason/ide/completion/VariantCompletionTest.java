package com.reason.ide.completion;

import java.util.*;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.files.RmlFileType;

@SuppressWarnings("ConstantConditions")
public class VariantCompletionTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testVariant() {
        myFixture.configureByText("VariantCompletion.re", "type color = | Black | Red;");
        myFixture.configureByText(RmlFileType.INSTANCE, "VariantCompletion.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(3, elements);
        assertContainsElements(elements, "color", "Black", "Red");
    }
}
