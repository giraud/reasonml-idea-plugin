package com.reason.ide.completion;

import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class RecordCompletion_RES_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("A.res", "type r = { a: float, b: int }");
        configureCode("B.res", "let b: A.r = { a: 1., b: 2 }\n b.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "a", "b");
    }

    @Test
    public void test_deep() {
        configureCode("A.res", """
                let record = { a:1, b:{ c:{ d:2, e:3 }, f:4 }}
                let _ = record.b.c.<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "d", "e");
        assertSize(2, elements);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/453
    @Test
    public void test_GH_453_mixin() {
        configureCode("A.res", """
                let default = {abc: 1, def: 2}
                let x = {...default, <caret>}
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "abc", "def");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/453
    @Test
    public void test_GH_453_mixin_2() {
        configureCode("A.res", """
                let default = {abc1: 1, abc2: 2, def: 2}
                let x = {...default, ab<caret>}
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "abc1", "abc2");
    }
}
