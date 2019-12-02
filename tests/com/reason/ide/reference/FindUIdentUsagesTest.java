package com.reason.ide.reference;

import com.intellij.usageView.UsageInfo;
import com.reason.ide.ORBasePlatformTestCase;

import java.util.Collection;

@SuppressWarnings("ConstantConditions")
public class FindUIdentUsagesTest extends ORBasePlatformTestCase {

    public void testException() {
        myFixture.configureByText("A.re", "exception Exception<caret>Name; raise(ExceptionName);");

        Collection<UsageInfo> usages = myFixture.testFindUsages("A.re");
        assertSize(1, usages);
        assertEquals("(ExceptionName)", usages.iterator().next().getElement().getParent().getText());
    }

}
