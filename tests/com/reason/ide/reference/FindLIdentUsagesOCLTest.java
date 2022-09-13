package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class FindLIdentUsagesOCLTest extends ORBasePlatformTestCase {
    @Test
    public void test_from_module() {
        configureCode("FLIA.ml", "let x<caret> = 1");
        configureCode("FLIB.ml", "let y = FLIA.x + 2;");

        Collection<UsageInfo> usages = myFixture.testFindUsages("FLIA.ml");
        assertSize(1, usages);
    }

    @Test
    public void test_same_module() {
        configureCode("FLIC.ml", "let x<caret> = 1\n let y = x + 1");

        List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("FLIC.ml");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

    @Test
    public void test_val() {
        configureCode("A.mli", "val x<caret>: int");
        configureCode("B.ml", "let y = A.x + 2");

        List<UsageInfo> usages = findUsages("A.mli");
        assertSize(1, usages);
        assertInstanceOf(usages.get(0).getElement().getParent(), PsiLetBinding.class);
    }
}
