package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class Jsx3NameCompletion_RES_Test extends ORBasePlatformTestCase {
    @Test
    public void test_outside_components() {
        configureCode("DialogHeader.res", "@react.component let make = () => { <div/> }");
        configureCode("DialogFooter.res", "@react.component let make = () => { <div/> }");
        configureCode("Dialog.res", "@react.component let make = () => <Dia<caret>");

        myFixture.completeBasic();
        List<String> completions = myFixture.getLookupElementStrings();

        assertContainsElements(completions, "DialogHeader", "DialogFooter");
        assertSize(2, completions);
    }

    @Test
    public void test_dont_display_properties() {
        configureCode("DialogHeader.res", "@react.component let make = () => { <div/> }");
        configureCode("Dummy.res", "let _ = <<caret>Dialog");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> completions = myFixture.getLookupElementStrings();

        assertEquals(1, completions.size());
        assertEquals("DialogHeader", completions.get(0));
    }
}
