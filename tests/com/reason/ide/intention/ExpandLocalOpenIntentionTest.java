package com.reason.ide.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.files.RmlFileType;

@SuppressWarnings("ConstantConditions")
public class ExpandLocalOpenIntentionTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testBasic() {
        myFixture.configureByText(RmlFileType.INSTANCE, "Js.Promise.(<caret>Api.all());");
        IntentionAction expandAction = myFixture.getAvailableIntention("Expand local open");
        myFixture.launchAction(expandAction);

        myFixture.checkResult("{ open Js.Promise; Api.all(); };");
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/67
    public void testInner() {
        myFixture.configureByText(RmlFileType.INSTANCE, "Js.Promise.(<caret>Api.all() |> then_(result => if (!result) { Js.log(result); () }));");
        IntentionAction expandAction = myFixture.getAvailableIntention("Expand local open");
        myFixture.launchAction(expandAction);

        myFixture.checkResult("{ open Js.Promise; Api.all() |> then_(result => if (!result) { Js.log(result); () }); };");
    }

}
