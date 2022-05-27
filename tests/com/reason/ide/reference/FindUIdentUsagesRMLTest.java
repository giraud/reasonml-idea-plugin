package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindUIdentUsagesRMLTest extends ORBasePlatformTestCase {
    public void test_exception() {
        configureCode("A.re", "exception Exception<caret>Name; raise(ExceptionName);");

        List<UsageInfo> usages = findUsages("A.re");
        assertEquals("(ExceptionName)", usages.get(0).getElement().getParent().getText());
    }

    public void test_module() {
        configureCode("A.re", "module M<caret>; let x = M.x;");

        List<UsageInfo> usages = findUsages("A.re");
        assertEquals("M.x", usages.get(0).getElement().getParent().getText());
    }

    private @NotNull List<UsageInfo> findUsages(String fileName) {
        return (List<UsageInfo>) myFixture.testFindUsages(fileName);
    }
}
