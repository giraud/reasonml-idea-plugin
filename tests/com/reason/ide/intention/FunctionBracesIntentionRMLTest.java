package com.reason.ide.intention;

import com.intellij.codeInsight.intention.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class FunctionBracesIntentionRMLTest extends ORBasePlatformTestCase {
    public static final String ADD_BRACES = "Add braces to blockless function";

    @Test
    public void test_basic() {
        configureCode("A.re", "let add = (x, y) => <caret>x + y;");
        IntentionAction bracesAction = myFixture.getAvailableIntention(ADD_BRACES);
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("let add = (x, y) => { x + y; };");
    }

    @Test
    public void test_withSignature() {
        configureCode("A.re", "let add = (x: int => int, y: int) => <caret>x + y;");
        IntentionAction bracesAction = myFixture.getAvailableIntention(ADD_BRACES);
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("let add = (x: int => int, y: int) => { x + y; };");
    }

    @Test
    public void test_jsx2ComponentFunction() {
        configureCode("A.re", "let make = (children) => { ...component, render: self => <di<caret>v/>, };");
        IntentionAction bracesAction = myFixture.getAvailableIntention(ADD_BRACES);
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("let make = (children) => { ...component, render: self => { <div/>; }, };");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/67
    @Test
    public void test_GH_67() {
        configureCode("A.re", "Js.Promise.( Api.all() |> then_(result => <caret>Js.log(result)) );");
        IntentionAction bracesAction = myFixture.getAvailableIntention(ADD_BRACES);
        myFixture.launchAction(bracesAction);

        myFixture.checkResult("Js.Promise.( Api.all() |> then_(result => { Js.log(result); }) );");
    }
}
