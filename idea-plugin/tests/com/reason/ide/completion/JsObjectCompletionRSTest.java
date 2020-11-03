package com.reason.ide.completion;

import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class JsObjectCompletionRSTest extends ORBasePlatformTestCase {

  public void test_basic() {
    configureCode("JsObj.res", "let oo = {\"asd\": 1, \"qwe\": 2}");
    configureCode("Dummy.res", "open JsObj; oo##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(2, elements);
    assertContainsElements(elements, "asd", "qwe");
  }

  public void test_deep() {
    configureCode("JsObj.res", "let oo = {\"first\": {\"deep\": true},\"deep\": {\"asd\": 1} }");
    configureCode("Dummy.res", "open JsObj; oo##deep##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(1, elements);
    assertContainsElements(elements, "asd");
  }

  public void test_deep_incorrect() {
    configureCode("JsObj.res", "let oo = {\"first\": {\"deep\": true},\"deep\": {\"asd\": 1} }");
    configureCode("Dummy.res", "open JsObj; oo##foo##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertEmpty(elements);
  }

  public void test_deepJsField() {
    configureCode("JsObj.res", "let o = {\"asd\": 1};\nlet oo = {\"deep\": o};");
    configureCode("Dummy.res", "open JsObj; oo##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(1, elements);
    assertContainsElements(elements, "deep");
  }

  public void test_deepJsFieldCompletionOrder() {
    configureCode("JsObj.re", "let oo = {\"first\": {\"deep\": true},\"deep\": {\"asd\": 1} }");
    configureCode("Dummy.re", "open JsObj; oo##first##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(1, elements);
    assertContainsElements(elements, "deep");
  }

  public void test_composed() {
    configureCode("JsObj.res", "let o = {\"f22\": 222}; let oo = {\"f1\": {\"f11\": 111}, \"f2\": o,\"f3\": {\"f33\": 333} }");
    configureCode("Dummy.res", "open JsObj; oo##f2##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(1, elements);
    assertContainsElements(elements, "f22");
  }

  public void test_composed_2() {
    configureCode("JsObj2.res", "let o = {\"f22\": 222}; let oo = {\"f1\": {\"f11\": 111}, \"f2\": o,\"f3\": {\"f33\": 333} }");
    configureCode("JsObj.res", "let oo = {\"f1\": {\"f11\": 111}, \"f2\": JsObj2.o,\"f3\": {\"f33\": 333} }");
    configureCode("Dummy.res", "open JsObj; oo##f2##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(1, elements);
    assertContainsElements(elements, "f22");
  }

  public void test_composed_3() {
    configureCode("JsObj.res", "let o = {\"ooo\": o, \"f22\": 222}; let oo = {\"f1\": o, \"f2\": o,\"f3\": {\"f33\": 333} }");
    configureCode("Dummy.res", "open JsObj; oo##f2##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(2, elements);
    assertContainsElements(elements, "ooo", "f22");
  }

  public void test_path() {
    configureCode("A.res", "let o = {\"oo\": 1};");
    configureCode("B.res", "let o = {\"oooo\": 1};");
    configureCode("Dummy.res", "open A; o##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(1, elements);
    assertContainsElements(elements, "oo");
  }

  public void test_alias() {
    configureCode("A.res", "let o = {\"oo\": 1};");
    configureCode("Alias.res", "module AA = A;");
    configureCode("B.res", "Alias.AA.o##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(1, elements);
    assertContainsElements(elements, "oo");
  }

  public void test_with_type() {
    configureCode("A.res", "type t = {. \"a\": int };");
    configureCode("B.res", "open A; let y:t = x; y##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(1, elements);
    assertContainsElements(elements, "a");
  }
}
