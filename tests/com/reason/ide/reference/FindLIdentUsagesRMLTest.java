package com.reason.ide.reference;

import com.intellij.usageView.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FindLIdentUsagesRMLTest extends ORBasePlatformTestCase {
    public void test_let() {
        configureCode("A.re", "let x<caret> = 1; let z = x + 2;");

        List<UsageInfo> usages = findUsages("A.re");
        assertSize(1, usages);
        assertInstanceOf(usages.get(0).getElement().getParent(), PsiLetBinding.class);
    }

    public void test_type() {
        configureCode("A.re", "type t<caret>; type x = t;");

        List<UsageInfo> usages = findUsages("A.re");
        assertSize(1, usages);
        assertInstanceOf(usages.get(0).getElement().getParent(), PsiTypeBinding.class);
    }

    public void test_external() {
        configureCode("A.re", "external width<caret> : unit => int = \"\"; let x = width();");

        List<UsageInfo> usages = findUsages("A.re");
        assertSize(1, usages);
        assertInstanceOf(usages.get(0).getElement().getParent(), PsiFunctionCall.class);
    }

    public void test_from_module() {
        configureCode("FLIA.re", "let x<caret> = 1;");
        configureCode("FLIB.re", "let y = FLIA.x + 2;");

        List<UsageInfo> usages = findUsages("FLIA.re");
        assertSize(1, usages);
    }

    public void test_same_module() {
        configureCode("FLIC.re", "let x<caret> = 1; let y = x + 1;");

        List<UsageInfo> usages = findUsages("FLIC.re");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

    public void test_module_signature() {
        configureCode("A.rei", "module B: { type t<caret>; let toString: t => string; }; module C: { type t; let toString: t => string; };");

        List<UsageInfo> usages = findUsages("A.rei");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("t", usageInfo.getElement().getParent().getText());
        assertEquals("A.B.toString", ((PsiQualifiedPathElement) usageInfo.getElement().getParent().getParent().getParent()).getQualifiedName());
    }
}
