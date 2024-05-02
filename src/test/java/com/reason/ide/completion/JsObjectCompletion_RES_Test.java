package com.reason.ide.completion;

import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class JsObjectCompletion_RES_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic_bracket() {
        configureCode("pervasives.mli", "let max_float: float->float->float");
        configureCode("JsObj.res", """
                let oo = {"abc": 1, "def": 2}""");
        configureCode("Dummy.res", """
                let x =1
                JsObj.oo[<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "\"abc\"", "\"def\"");
    }

    @Test
    public void test_basic_string() {
        configureCode("JsObj.res", "let oo = {\"abc\": 1, \"def\": 2}");
        configureCode("Dummy.res", "JsObj.oo[\"<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "\"abc\"", "\"def\"");
    }

    @Test
    public void test_deep() {
        configureCode("JsObj.res", """
                let oo = {"first": {"deep": true}, "deep": {"other": {"asd": 1} } }""");
        configureCode("Dummy.res", """
                open JsObj
                oo["deep"]["other"][<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "\"asd\"");
        assertSize(1, elements);
    }

    @Test
    public void test_deep_field() {
        configureCode("JsObj.res", """
                let o = {"asd": 1}
                let oo = {"deep": o}""");
        configureCode("Dummy.res", """
                open JsObj
                oo[<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assert elements != null;
        assertSize(1, elements);
        assertContainsElements(elements, "\"deep\"");
    }

    @Test
    public void test_deep_field_completion_order() {
        configureCode("JsObj.res", """
                let oo = {"first": {"deep": true}, "deep": {"asd": 1} }""");
        configureCode("Dummy.res", """
                open JsObj
                oo["first"][<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "\"deep\"");
        assertSize(1, elements);
    }

    @Test
    public void test_composed() {
        configureCode("A.res", """
                let o = {"f22": 222}
                let oo = {"f1": {"f11": 111}, "f2": o,"f3": {"f33": 333} }""");
        configureCode("B.res", """
                open A
                let oo = {"f1": {"f11": 111}, "f2": o, "f3": {"f33": 333} }""");
        configureCode("Dummy.res", """
                open B
                oo["f2"][<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "\"f22\"");
        assertSize(1, elements);
    }

    @Test
    public void test_composed_2() {
        configureCode("JsObj2.res", """
                let o = {"f22": 222}
                let oo = {"f1": {"f11": 111}, "f2": o,"f3": {"f33": 333} }""");
        configureCode("JsObj.res", """
                let oo = {"f1": {"f11": 111}, "f2": JsObj2.o, "f3": {"f33": 333} }""");
        configureCode("Dummy.res", """
                open JsObj
                oo["f2"][<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "\"f22\"");
        assertSize(1, elements);
    }

    @Test
    public void test_composed_3() {
        configureCode("JsObj.res", """
                let o = {"ooo": o, "f22": 222}
                let oo = {"f1": o, "f2": o, "f3": {"f33": 333}}""");
        configureCode("Dummy.res", """
                open JsObj
                oo["f2"][<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "\"ooo\"", "\"f22\"");
        assertSize(2, elements);
    }

    @Test
    public void test_path() {
        configureCode("A.res", """
                let o = {"oo": 1}""");
        configureCode("B.res", """
                let o = {"oooo": 1}""");
        configureCode("Dummy.res", """
                open A
                o[<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertContainsElements(elements, "\"oo\"");
    }

    @Test
    public void test_alias() {
        configureCode("A.res", "let o = {\"oo\": 1}");
        configureCode("Alias.res", "module AA = A");
        configureCode("B.res", "Alias.AA.o[<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "\"oo\"");
        assertSize(1, elements);
    }

    @Test
    public void test_with_type() {
        configureCode("A.res", "type t = {\"a\": int }");
        configureCode("B.res", """
                open A
                let y: t = x
                y[<caret>""");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "\"a\"");
        assertSize(1, elements);
    }
}
