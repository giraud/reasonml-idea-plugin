package com.reason.ide.refactor;

import com.reason.ide.*;
import org.junit.*;

public class IntroduceVariableHandler_RES_Test extends ORBasePlatformTestCase {
    @Test
    public void test_let_caret() {
        configureCode("A.res", "let x = 1<caret>");
        new ORIntroduceVariableHandler().invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("let x = { let pop = 1\n pop }", true);
    }

    @Test
    public void test_let_selection() {
        configureCode("A.res", "let x = <selection>1</selection>");
        new ORIntroduceVariableHandler().invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("let x = { let pop = 1\n pop }", true);
    }
}
