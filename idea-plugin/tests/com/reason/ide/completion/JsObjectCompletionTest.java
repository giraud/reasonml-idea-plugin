package com.reason.ide.completion;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.RmlFileType;
import java.util.*;

public class JsObjectCompletionTest extends ORBasePlatformTestCase {

  public void test_Rml_basicJsFieldCompletion() {
    configureCode("JsObj.re", "let oo = {\"asd\": 1, \"qwe\": 2}");
    myFixture.configureByText(RmlFileType.INSTANCE, "open JsObj; oo##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assert elements != null;
    assertSize(2, elements);
    assertContainsElements(elements, "asd", "qwe");
  }

  public void testDeepJsFieldCompletion() {
    configureCode("JsObj.re", "let oo = {\"first\": {\"deep\": true},\"deep\": {\"asd\": 1} }");
    configureCode("Dummy.re", "open JsObj; oo##deep##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assert elements != null;
    assertSize(1, elements);
    assertContainsElements(elements, "asd");
  }

  public void testDeepJsFieldCompletion2() {
    myFixture.configureByText("JsObj.re", "let o = {\"asd\": 1};\nlet oo = {\"deep\": o};");
    myFixture.configureByText(RmlFileType.INSTANCE, "open JsObj; oo##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assert elements != null;
    assertSize(1, elements);
    assertContainsElements(elements, "deep");
  }

  public void testDeepJsFieldCompletionOrder() {
    myFixture.configureByText(
        "JsObj.re", "let oo = {\"first\": {\"deep\": true},\"deep\": {\"asd\": 1} }");
    myFixture.configureByText(RmlFileType.INSTANCE, "open JsObj; oo##first##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assert elements != null;
    assertSize(1, elements);
    assertContainsElements(elements, "deep");
  }

  public void testComposedJsObject() {
    configureCode(
        "JsObj.re",
        "let o = {\"f22\": 222}; let oo = {\"f1\": {\"f11\": 111}, \"f2\": o,\"f3\": {\"f33\": 333} }");
    myFixture.configureByText(RmlFileType.INSTANCE, "open JsObj; oo##f2##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assert elements != null;
    assertSize(1, elements);
    assertContainsElements(elements, "f22");
  }

  public void testComposedJsObject2() {
    myFixture.configureByText(
        "JsObj2.re",
        "let o = {\"f22\": 222}; let oo = {\"f1\": {\"f11\": 111}, \"f2\": o,\"f3\": {\"f33\": 333} }");
    myFixture.configureByText(
        "JsObj.re", "let oo = {\"f1\": {\"f11\": 111}, \"f2\": JsObj2.o,\"f3\": {\"f33\": 333} }");
    myFixture.configureByText(RmlFileType.INSTANCE, "open JsObj; oo##f2##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assert elements != null;
    assertSize(1, elements);
    assertContainsElements(elements, "f22");
  }

  public void testComposedJsObject3() {
    myFixture.configureByText(
        "JsObj.re",
        "let o = {\"ooo\": o, \"f22\": 222}; let oo = {\"f1\": o, \"f2\": o,\"f3\": {\"f33\": 333} }");
    myFixture.configureByText(RmlFileType.INSTANCE, "open JsObj; oo##f2##<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assert elements != null;
    assertSize(2, elements);
    assertContainsElements(elements, "ooo", "f22");
  }
}
