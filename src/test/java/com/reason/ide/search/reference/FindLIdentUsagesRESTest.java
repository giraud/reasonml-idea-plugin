package com.reason.ide.search.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class FindLIdentUsagesRESTest extends ORBasePlatformTestCase {
    @Test
    public void test_from_module() {
        configureCode("FLIA.res", "let x<caret> = 1");
        configureCode("FLIB.res", "let y = FLIA.x + 2");

        Collection<UsageInfo> usages = myFixture.testFindUsages("FLIA.res");
        assertSize(1, usages);
    }

    @Test
    public void test_same_module() {
        configureCode("FLIC.res", "let x<caret> = 1\n let y = x + 1");

        List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("FLIC.res");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

    @Test
    public void test_module_signature() {
        configureCode("A.resi", "module B: { type t<caret>\n let toString: t => string\n }\n module C: { type t\n let toString: t => string\n }");

        List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("A.resi");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("t", usageInfo.getElement().getParent().getText());
        assertEquals("A.B.toString", ((RPsiQualifiedPathElement) usageInfo.getElement().getParent().getParent().getParent()).getQualifiedName());
    }

/*    @Test    TODO
    public void test_record() {
        configureCode("A.res", "type t = { f1: bool, f2<caret>: int }\n let x = { f1: true, f2: 421 }");

        List<UsageInfo> usages = findUsages("A.res");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("A.x.f2", ((RPsiQualifiedPathElement) usageInfo.getValues().getParent()).getQualifiedName());
    }*/
}
