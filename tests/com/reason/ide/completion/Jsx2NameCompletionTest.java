package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.files.RmlFileType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Jsx2NameCompletionTest extends LightPlatformCodeInsightFixtureTestCase {

    @NotNull
    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/lang/component";
    }

    public void testShouldDisplayComponents() {
        myFixture.configureByFiles("Component.re", "CompMessage.re");
        myFixture.configureByText(RmlFileType.INSTANCE, "let _ = <Comp<caret>");

        myFixture.completeBasic();

        List<String> completionElements = myFixture.getLookupElementStrings();
        assertSize(2, completionElements);
        assertContainsElements(completionElements, "Component", "CompMessage");
    }

    public void testShouldNotDisplayProperties() {
        myFixture.configureByFiles("Component.re");
        myFixture.configureByText(RmlFileType.INSTANCE, "let _ = <<caret>Component");

        myFixture.complete(CompletionType.BASIC, 1);

        List<String> strings = myFixture.getLookupElementStrings();
        assertEquals(1, strings.size());
        assertEquals("Component", strings.get(0));
    }
}
