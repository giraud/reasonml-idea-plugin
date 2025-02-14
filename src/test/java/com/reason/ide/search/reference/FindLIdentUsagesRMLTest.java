package com.reason.ide.search.reference;

import com.intellij.openapi.util.*;
import com.intellij.usageView.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindLIdentUsagesRMLTest extends ORBasePlatformTestCase {
    @Test
    public void test_let() {
        configureCode("A.re", "let x<caret> = 1; let z = x + 2;");

        List<UsageInfo> usages = findUsages("A.re");
        assertSize(1, usages);
        assertInstanceOf(usages.getFirst().getElement().getParent(), RPsiLetBinding.class);
    }

    @Test
    public void test_type() {
        configureCode("A.re", "type t<caret>; type x = t;");

        List<UsageInfo> usages = findUsages("A.re");
        assertSize(1, usages);
        assertInstanceOf(usages.getFirst().getElement().getParent(), RPsiTypeBinding.class);
    }

    @Test
    public void test_external() {
        configureCode("A.re", "external width<caret> : unit => int = \"\"; let x = width();");

        List<UsageInfo> usages = findUsages("A.re");
        assertSize(1, usages);
        assertInstanceOf(usages.getFirst().getElement().getParent(), RPsiFunctionCall.class);
    }

    @Test
    public void test_from_module() {
        configureCode("FLIA.re", "let x<caret> = 1;");
        configureCode("FLIB.re", "let y = FLIA.x + 2;");

        List<UsageInfo> usages = findUsages("FLIA.re");
        assertSize(1, usages);
    }

    @Test
    public void test_same_module() {
        configureCode("FLIC.re", "let x<caret> = 1; let y = x + 1;");

        List<UsageInfo> usages = findUsages("FLIC.re");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

    @Test
    public void test_module_signature() {
        configureCode("A.rei", "module B: { type t<caret>; let toString: t => string; }; module C: { type t; let toString: t => string; };");

        List<UsageInfo> usages = findUsages("A.rei");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals("t", usageInfo.getElement().getParent().getText());
        assertEquals("A.B.toString", ((RPsiQualifiedPathElement) usageInfo.getElement().getParent().getParent().getParent()).getQualifiedName());
    }

    @Test
    public void test_record_field() {
        configureCode("A.re", """
                type t = { f1: bool, f2<caret>: int };
                let x = { f1: true, f2: 421 };
                """);

        List<UsageInfo> usages = findUsages("A.re");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals("A.x.f2", ((RPsiQualifiedPathElement) usageInfo.getElement().getParent()).getQualifiedName());
    }

    @Test
    public void test_object_field() {
        configureCode("A.re", """
                let obj = { "f1": true, "f2<caret>": 421 };
                let _ = obj##f2;
                """);

        List<UsageInfo> usages = findUsages("A.re");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals(TextRange.create(50, 52), usageInfo.getSegment());
    }

    @Test
    public void test_destructuration() {
        configureCode("A.re", """
                let (dialogStatus, setDialog<caret>Status) = x;
                let _ = () => setDialogStatus();
                """);

        List<UsageInfo> usages = findUsages("A.re");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.getFirst();
        assertEquals(TextRange.create(55, 70), usageInfo.getSegment());
    }
}
