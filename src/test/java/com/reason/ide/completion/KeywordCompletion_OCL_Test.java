package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class KeywordCompletion_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("B.ml", "<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "open", "include", "module", "type", "let", "external", "exception");
    }
}
