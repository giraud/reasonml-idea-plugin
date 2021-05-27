package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindUIdentUsagesOCLTest extends ORBasePlatformTestCase {
    public void test_exception() {
        configureCode("A.ml", "exception Exception<caret>Name\n let _ = raise ExceptionName");

        Collection<UsageInfo> usages = myFixture.testFindUsages("A.ml");
        assertSize(1, usages);
        assertEquals("raise ExceptionName", usages.iterator().next().getElement().getParent().getText());
    }
}
