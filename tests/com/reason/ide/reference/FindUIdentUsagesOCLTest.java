package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindUIdentUsagesOCLTest extends ORBasePlatformTestCase {
    public void test_exception() {
        configureCode("A.ml", "exception Exception<caret>Name\n let _ = raise ExceptionName");

        Collection<UsageInfo> usages = myFixture.testFindUsages("A.ml");
        assertSize(1, usages);
        assertEquals("raise ExceptionName", usages.iterator().next().getElement().getParent().getText());
    }

    public void test_module() {
        configureCode("A.ml", "module M<caret>\n let x = M.x");

        List<UsageInfo> usages = findUsages("A.ml");
        assertEquals("M.x", usages.get(0).getElement().getParent().getText());
    }

    public void test_val() {
        configureCode("A.ml", "val x<caret>: int\n let y = x + 2;");

        List<UsageInfo> usages = findUsages("A.ml");
        assertSize(1, usages);
        assertInstanceOf(usages.get(0).getElement().getParent(), PsiLetBinding.class);
    }
}
