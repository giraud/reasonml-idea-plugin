package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindLIdentUsagesRMLTest extends ORBasePlatformTestCase {
    public void test_from_module() {
        configureCode("FLIA.re", "let x<caret> = 1;");
        configureCode("FLIB.re", "let y = FLIA.x + 2;");

        Collection<UsageInfo> usages = myFixture.testFindUsages("FLIA.re");
        assertSize(1, usages);
    }

    public void test_same_module() {
        configureCode("FLIC.re", "let x<caret> = 1; let y = x + 1;");

        List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("FLIC.re");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

    public void test_module_signature() {
        configureCode("A.rei", "module B: { type t<caret>; let toString: t => string; }; module C: { type t; let toString: t => string; };");

        List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("A.rei");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("t", usageInfo.getElement().getParent().getText());
        assertEquals("A.B.toString", ((PsiQualifiedPathElement) usageInfo.getElement().getParent().getParent().getParent()).getQualifiedName());
    }
}
