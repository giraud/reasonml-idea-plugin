package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindUIdentUsagesRMLTest extends ORBasePlatformTestCase {
    public void test_exception() {
        configureCode("A.re", "exception Exception<caret>Name; raise(ExceptionName);");

        Collection<UsageInfo> usages = myFixture.testFindUsages("A.re");
        assertSize(1, usages);
        assertEquals("(ExceptionName)", usages.iterator().next().getElement().getParent().getText());
    }
}
