package com.reason.ide.completion;

import com.reason.ide.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class JsObjectCompletion_RML_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("JsObj.re", "let oo = {\"asd\": 1, \"qwe\": 2}");
        configureCode("Dummy.re", "open JsObj; oo##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "asd", "qwe");
    }

    @Test
    public void test_deep() {
        configureCode("JsObj.re", "let oo = {\"first\": {\"deep\": true}, \"deep\": {\"other\": {\"asd\": 1} } }");
        configureCode("Dummy.re", "open JsObj; oo##deep##other##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertContainsElements(elements, "asd");
    }

    @Test
    public void test_deepJsField() {
        configureCode("JsObj.re", "let o = {\"asd\": 1};\nlet oo = {\"deep\": o};");
        configureCode("Dummy.re", "open JsObj; oo##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assert elements != null;
        assertSize(1, elements);
        assertContainsElements(elements, "deep");
    }

    @Test
    public void test_deepJsFieldCompletionOrder() {
        configureCode("JsObj.re", "let oo = {\"first\": {\"deep\": true},\"deep\": {\"asd\": 1} }");
        configureCode("Dummy.re", "open JsObj; oo##first##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertContainsElements(elements, "deep");
    }

    @Test
    public void test_composed() {
        configureCode("A.re", "let o = {\"f22\": 222}; let oo = {\"f1\": {\"f11\": 111}, \"f2\": o,\"f3\": {\"f33\": 333} }");
        configureCode("B.re", "open A; let oo = {\"f1\": {\"f11\": 111}, \"f2\": o, \"f3\": {\"f33\": 333} }");
        configureCode("Dummy.re", "open B; oo##f2##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertContainsElements(elements, "f22");
    }

    @Test
    public void test_composed_2() {
        configureCode("JsObj2.re", "let o = {\"f22\": 222}; let oo = {\"f1\": {\"f11\": 111}, \"f2\": o,\"f3\": {\"f33\": 333} }");
        configureCode("JsObj.re", "let oo = {\"f1\": {\"f11\": 111}, \"f2\": JsObj2.o,\"f3\": {\"f33\": 333} }");
        configureCode("Dummy.re", "open JsObj; oo##f2##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "f22");
        assertSize(1, elements);
    }

    @Test
    public void test_composed_3() {
        configureCode("JsObj.re", "let o = {\"ooo\": o, \"f22\": 222}; let oo = {\"f1\": o, \"f2\": o,\"f3\": {\"f33\": 333} }");
        configureCode("Dummy.re", "open JsObj; oo##f2##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(2, elements);
        assertContainsElements(elements, "ooo", "f22");
    }

    @Test
    public void test_path() {
        configureCode("A.re", "let o = {\"oo\": 1};");
        configureCode("B.re", "let o = {\"oooo\": 1};");
        configureCode("Dummy.re", "open A; o##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertContainsElements(elements, "oo");
    }

    @Test
    public void test_alias() {
        configureCode("A.re", "let o = {\"oo\": 1};");
        configureCode("Alias.re", "module AA = A;");
        configureCode("B.re", "Alias.AA.o##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertContainsElements(elements, "oo");
    }

    @Test
    public void test_with_type() {
        configureCode("A.re", "type t = {. \"a\": int };");
        configureCode("B.re", "open A; let y:t = x; y##<caret>");

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "a");
        assertSize(1, elements);
    }
}
