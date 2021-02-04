package com.reason.ide.reference;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.usageView.UsageInfo;
import java.util.Collection;

@SuppressWarnings("ConstantConditions")
public class FindUIdentUsagesTest extends BasePlatformTestCase {

  public void testException() {
    myFixture.configureByText("A.re", "exception Exception<caret>Name; raise(ExceptionName);");

    Collection<UsageInfo> usages = myFixture.testFindUsages("A.re");
    assertSize(1, usages);
    assertEquals("(ExceptionName)", usages.iterator().next().getElement().getParent().getText());
  }
}
