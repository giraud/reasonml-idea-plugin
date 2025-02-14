package com.reason.ide.search.reference;

import com.intellij.openapi.util.*;
import com.intellij.usageView.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
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
        UsageInfo usageInfo = usages.getFirst();
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

    @Test
    public void test_val() {
        configureCode("A.mli", "val x<caret>: int");
        configureCode("B.ml", "let y = A.x + 2");

        List<UsageInfo> usages = findUsages("A.mli");
        assertSize(1, usages);
        assertInstanceOf(usages.getFirst().getElement().getParent(), RPsiLetBinding.class);
    }

    @Test
    public void test_destructuration() {
        configureCode("A.ml", """
                let (dialogStatus,setDialog<caret>Status) = x
                let _ = fun ()  -> setDialogStatus ()
                """);

        List<UsageInfo> usages = findUsages("A.ml");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals(TextRange.create(58, 73), usageInfo.getSegment());
    }
}
