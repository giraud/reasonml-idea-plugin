package com.reason.ide.completion;

import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class RecordCompletionRESTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("A.res", "type r = { a: float, b: int };");
        configureCode("B.res", "let b: A.r = { a: 1., b: 2 }; b.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "a", "b");
    }
}
