package com.reason.ide.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.RmlFileType;

@SuppressWarnings("ConstantConditions")
public class ExpandLocalOpenIntentionRMLTest extends ORBasePlatformTestCase {
    public void testBasic() {
        myFixture.configureByText(RmlFileType.INSTANCE, "let x = Js.Promise.(<caret>Api.all());");
        IntentionAction expandAction = myFixture.getAvailableIntention("Expand local open");
        myFixture.launchAction(expandAction);

        myFixture.checkResult("let x = { open Js.Promise; Api.all(); };");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/67
    public void testInner() {
        myFixture.configureByText(RmlFileType.INSTANCE, "Js.Promise.(<caret>Api.all() |> then_(result => if (!result) { Js.log(result); () }));");
        IntentionAction expandAction = myFixture.getAvailableIntention("Expand local open");
        myFixture.launchAction(expandAction);

        myFixture.checkResult("{ open Js.Promise; Api.all() |> then_(result => if (!result) { Js.log(result); () }); };");
    }
}
