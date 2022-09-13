package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindLIdentUsagesRESTest extends ORBasePlatformTestCase {
    public void test_from_module() {
        configureCode("FLIA.res", "let x<caret> = 1");
        configureCode("FLIB.res", "let y = FLIA.x + 2");

        Collection<UsageInfo> usages = myFixture.testFindUsages("FLIA.res");
        assertSize(1, usages);
    }

    public void test_same_module() {
        configureCode("FLIC.res", "let x<caret> = 1\n let y = x + 1");

        List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("FLIC.res");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

    public void test_module_signature() {
        configureCode("A.resi", "module B: { type t<caret>\n let toString: t => string\n }\n module C: { type t\n let toString: t => string\n }");

        List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("A.resi");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("t", usageInfo.getElement().getParent().getText());
        assertEquals("A.B.toString", ((PsiQualifiedPathElement) usageInfo.getElement().getParent().getParent().getParent()).getQualifiedName());
    }
}
