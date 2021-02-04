package com.reason.ide.completion;

import com.reason.ide.ORBasePlatformTestCase;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class VariantCompletionTest extends ORBasePlatformTestCase {

  public void test_Rml_variant() {
    configureCode("A.re", "type color = | Black | Red;");
    configureCode("B.re", "A.<caret>");

    myFixture.completeBasic();
    List<String> elements = myFixture.getLookupElementStrings();

    assertSize(3, elements);
    assertContainsElements(elements, "color", "Black", "Red");
  }
}
