package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class KeywordCompletionRESTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("B.res", "<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "open", "include", "module", "type", "let", "external", "exception");
    }
}
