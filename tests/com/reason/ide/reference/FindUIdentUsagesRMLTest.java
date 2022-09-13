package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class FindUIdentUsagesRMLTest extends ORBasePlatformTestCase {
    @Test
    public void test_exception() {
        configureCode("A.re", "exception Exception<caret>Name; raise(ExceptionName);");

        List<UsageInfo> usages = findUsages("A.re");
        assertEquals("(ExceptionName)", usages.get(0).getElement().getParent().getText());
    }

    @Test
    public void test_module() {
        configureCode("A.re", "module M<caret>; let x = M.x;");

        List<UsageInfo> usages = findUsages("A.re");
        assertEquals("M.x", usages.get(0).getElement().getParent().getText());
    }
}
