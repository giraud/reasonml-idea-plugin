package com.reason.ide.completion;

import java.util.*;
import com.reason.ide.ORBasePlatformTestCase;

@SuppressWarnings("ConstantConditions")
public class RecordCompletionTest extends ORBasePlatformTestCase {

    public void test_Rml_record() {
        configureCode("A.re", "type r = { a: float, b: int };");
        configureCode("B.re", "let b: A.r = { a: 1., b: 2 }; b.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "a", "b");
    }

    public void test_Ns_record() {
        configureCode("A.res", "type r = { a: float, b: int };");
        configureCode("B.res", "let b: A.r = { a: 1., b: 2 }; b.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "a", "b");
    }
}
