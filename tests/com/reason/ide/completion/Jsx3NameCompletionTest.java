package com.reason.ide.completion;

import java.util.*;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.files.RmlFileType;

@SuppressWarnings("ConstantConditions")
public class Jsx3NameCompletionTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testShouldDisplayComponents() {
        myFixture.configureByText("DialogHeader.re", "[@react.component] let make = () => { <div/> };");
        myFixture.configureByText("DialogFooter.re", "[@react.component] let make = () => { <div/> };");

        myFixture.configureByText(RmlFileType.INSTANCE, "let _ = <Dia<caret>");
        myFixture.completeBasic();

        List<String> completions = myFixture.getLookupElementStrings();
        assertSize(2, completions);
        assertContainsElements(completions, "DialogHeader", "DialogFooter");
    }

    public void testShouldNotDisplayProperties() {
        myFixture.configureByText("DialogHeader.re", "[@react.component] let make = () => { <div/> };");

        myFixture.configureByText(RmlFileType.INSTANCE, "let _ = <<caret>Dialog");
        myFixture.complete(CompletionType.BASIC, 1);

        List<String> completions = myFixture.getLookupElementStrings();
        assertEquals(1, completions.size());
        assertEquals("DialogHeader", completions.get(0));
    }
}
