package com.reason.ide.completion;

import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class RecordCompletionRMLTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("A.re", "type r = { a: float, b: int };");
        configureCode("B.re", "let b: A.r = { a: 1., b: 2 }; b.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "a", "b");
    }
}
