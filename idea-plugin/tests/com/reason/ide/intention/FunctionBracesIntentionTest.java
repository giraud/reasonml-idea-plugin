package com.reason.ide.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.reason.ide.ORBasePlatformTestCase;

@SuppressWarnings("ConstantConditions")
public class FunctionBracesIntentionTest extends ORBasePlatformTestCase {
    public void testBasic() {
        configureCode("A.re", "let add = (x, y) => <caret>x + y;");
        IntentionAction bracesAction = myFixture.getAvailableIntention("Add braces to blockless function");
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("let add = (x, y) => { x + y; };");
    }

    public void test_withSignature() {
        configureCode("A.re", "let add = (x: int => int, y: int) => <caret>x + y;");
        IntentionAction bracesAction = myFixture.getAvailableIntention("Add braces to blockless function");
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("let add = (x: int => int, y: int) => { x + y; };");
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/67
    public void testInnerFunction() {
        configureCode("A.re", "Js.Promise.( Api.all() |> then_(result => <caret>Js.log(result)) );");
        IntentionAction bracesAction = myFixture.getAvailableIntention("Add braces to blockless function");
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("Js.Promise.( Api.all() |> then_(result => { Js.log(result); }) );");
    }

    public void testReactComponentFunction() {
        configureCode("A.re", "let make = (children) => { ...component, render: self => <di<caret>v/>, };");
        IntentionAction bracesAction = myFixture.getAvailableIntention("Add braces to blockless function");
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("let make = (children) => { ...component, render: self => { <div/>; }, };");
    }
}