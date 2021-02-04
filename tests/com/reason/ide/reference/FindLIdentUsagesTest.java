package com.reason.ide.reference;

import com.intellij.usageView.UsageInfo;
import com.reason.ide.ORBasePlatformTestCase;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindLIdentUsagesTest extends ORBasePlatformTestCase {

  public void test_Rml_fromModule() {
    configureCode("FLIA.re", "let x<caret> = 1;");
    configureCode("FLIB.re", "let y = FLIA.x + 2;");

    Collection<UsageInfo> usages = myFixture.testFindUsages("FLIA.re");
    assertSize(1, usages);
  }

  public void test_Rml_sameModule() {
    configureCode("FLIC.re", "let x<caret> = 1; let y = x + 1;");

    List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("FLIC.re");
    assertSize(1, usages);
    UsageInfo usageInfo = usages.get(0);
    assertEquals("x + 1", usageInfo.getElement().getParent().getText());
  }
}
