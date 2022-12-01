package com.reason.ide.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.RmlFileType;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class ExpandLocalOpenIntentionRMLTest extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        myFixture.configureByText(RmlFileType.INSTANCE, "let x = Js.Promise.(<caret>Api.all());");
        IntentionAction expandAction = myFixture.getAvailableIntention("Expand local open");
        myFixture.launchAction(expandAction);

        myFixture.checkResult("let x = { open Js.Promise; Api.all(); };");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/67
    @Test
    public void test_inner() {
        myFixture.configureByText(RmlFileType.INSTANCE, "Js.Promise.(<caret>Api.all() |> then_(result => if (!result) { Js.log(result); () }));");
        IntentionAction expandAction = myFixture.getAvailableIntention("Expand local open");
        myFixture.launchAction(expandAction);

        myFixture.checkResult("{ open Js.Promise; Api.all() |> then_(result => if (!result) { Js.log(result); () }); };");
    }
}
