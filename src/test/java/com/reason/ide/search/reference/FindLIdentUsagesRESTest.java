package com.reason.ide.search.reference;

import java.util.*;
import org.junit.Test;
import com.intellij.openapi.util.TextRange;
import com.intellij.usageView.UsageInfo;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.RPsiQualifiedPathElement;

@SuppressWarnings("DataFlowIssue")
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
        UsageInfo usageInfo = usages.getFirst();
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

    @Test
    public void test_module_signature() {
        configureCode("A.resi", "module B: { type t<caret>\n let toString: t => string\n }\n module C: { type t\n let toString: t => string\n }");

        List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("A.resi");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals("t", usageInfo.getElement().getParent().getText());
        assertEquals("A.B.toString", ((RPsiQualifiedPathElement) usageInfo.getElement().getParent().getParent().getParent()).getQualifiedName());
    }

    @Test
    public void test_record() {
        configureCode("A.res", """
                type t = { f1: bool, f2<caret>: int }
                let x = { f1: true, f2: 421 }
                """);

        List<UsageInfo> usages = findUsages("A.res");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals("A.x.f2", ((RPsiQualifiedPathElement) usageInfo.getElement().getParent()).getQualifiedName());
    }

    //@Test TODO make it work
    public void test_object_field() {
        configureCode("A.res", """
                let obj = { "f1": true, "f2<caret>": 421 }
                let _ = obj["f2"]
                """);

        List<UsageInfo> usages = findUsages("A.res");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals(TextRange.create(48, 52), usageInfo.getSegment());
    }

    @Test
    public void test_destructuration() {
        configureCode("A.res", """
                let (dialogStatus, setDialog<caret>Status) = x
                let _ = () => setDialogStatus()
                """);

        List<UsageInfo> usages = findUsages("A.res");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals(TextRange.create(54, 69), usageInfo.getSegment());
    }
}
