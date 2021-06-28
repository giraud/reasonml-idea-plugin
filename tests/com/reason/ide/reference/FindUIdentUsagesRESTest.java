package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindUIdentUsagesRESTest extends ORBasePlatformTestCase {
    public void test_exception() {
        configureCode("A.res", "exception Exception<caret>Name\n raise(ExceptionName)");

        Collection<UsageInfo> usages = myFixture.testFindUsages("A.res");
        assertSize(1, usages);
        assertEquals("(ExceptionName)", usages.iterator().next().getElement().getParent().getText());
    }
}
