package com.reason.ide.completion;

import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class RecordCompletion_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("A.ml", "type r = { a:float; b:int }");
        configureCode("B.ml", "let b: A.r = { a=1.; b=2 }\nlet _ = b.<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "a", "b");
        assertSize(2, elements);
    }

    @Test
    public void test_deep() {
        configureCode("A.ml", """
                let record = { a=1; b={ c={ d=2; e=3 }; f=4 }}
                let _ = record.b.c.<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "d", "e");
        assertSize(2, elements);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/453
    @Test
    public void test_GH_453_mixin() {
        configureCode("A.ml", """
                let default = { abc=1; def=2 }
                let x = { default with <caret>
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "abc", "def");
        assertSize(2, elements);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/453
    @Test
    public void test_GH_453_mixin_2() {
        configureCode("A.ml", """
                let default = { abc1=1; abc2=2; def=2 }
                let x = {default with ab<caret>};
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "abc1", "abc2");
        assertSize(2, elements);
    }
}
