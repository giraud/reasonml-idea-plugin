package com.reason.ide.completion;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.reason.ide.files.RmlFileType;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class VariantCompletionTest extends BasePlatformTestCase {

    public void testVariant() {
        myFixture.configureByText("VariantCompletion.re", "type color = | Black | Red;");
        myFixture.configureByText(RmlFileType.INSTANCE, "VariantCompletion.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(3, elements);
        assertContainsElements(elements, "color", "Black", "Red");
    }

}
