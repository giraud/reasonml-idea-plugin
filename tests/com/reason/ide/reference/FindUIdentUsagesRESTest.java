package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindUIdentUsagesRESTest extends ORBasePlatformTestCase {
    public void test_exception() {
        configureCode("A.res", "exception Exception<caret>Name\n raise(ExceptionName)");

        List<UsageInfo> usages = findUsages("A.res");
        assertSize(1, usages);
        assertEquals("(ExceptionName)", usages.iterator().next().getElement().getParent().getText());
    }

    public void test_module() {
        configureCode("A.res", "module M<caret>\n let x = M.x");

        List<UsageInfo> usages = findUsages("A.res");
        assertSize(1, usages);
        assertEquals("M.x", usages.get(0).getElement().getParent().getText());
    }
}
