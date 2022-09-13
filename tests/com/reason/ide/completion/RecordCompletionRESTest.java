package com.reason.ide.completion;

import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class RecordCompletionRESTest extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("A.res", "type r = { a: float, b: int }");
        configureCode("B.res", "let b: A.r = { a: 1., b: 2 }\n b.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "a", "b");
    }
}
