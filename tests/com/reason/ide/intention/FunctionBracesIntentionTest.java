package com.reason.ide.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.files.RmlFileType;

@SuppressWarnings("ConstantConditions")
public class FunctionBracesIntentionTest extends LightPlatformCodeInsightFixtureTestCase {
    public void testBasic() {
        myFixture.configureByText(RmlFileType.INSTANCE, "let add = (x, y) => <caret>x + y;");
        IntentionAction bracesAction = myFixture.getAvailableIntention("Add braces to blockless function");
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("let add = (x, y) => { x + y; };");
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/67
    public void testInnerFunction() {
        myFixture.configureByText(RmlFileType.INSTANCE, "Js.Promise.( Api.all() |> then_(result => <caret>Js.log(result)) );");
        IntentionAction bracesAction = myFixture.getAvailableIntention("Add braces to blockless function");
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("Js.Promise.( Api.all() |> then_(result => { Js.log(result); }) );");
    }

    public void testReactComponentFunction() {
        myFixture.configureByText(RmlFileType.INSTANCE, "let make = (children) => { ...component, render: self => <di<caret>v/>, };");
        IntentionAction bracesAction = myFixture.getAvailableIntention("Add braces to blockless function");
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("let make = (children) => { ...component, render: self => { <div/>; }, };");
    }
}