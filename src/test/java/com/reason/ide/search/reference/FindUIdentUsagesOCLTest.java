package com.reason.ide.search.reference;

import com.intellij.psi.util.*;
import com.intellij.usageView.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class FindUIdentUsagesOCLTest extends ORBasePlatformTestCase {
    @Test
    public void test_exception() {
        configureCode("A.ml", "exception Exception<caret>Name\n let _ = raise ExceptionName");

        Collection<UsageInfo> usages = myFixture.testFindUsages("A.ml");
        assertSize(1, usages);
        assertEquals("raise ExceptionName", usages.iterator().next().getElement().getParent().getText());
    }

    @Test
    public void test_module() {
        configureCode("A.ml", "module M<caret>\n let x = M.x");

        List<UsageInfo> usages = findUsages("A.ml");
        assertEquals("M.x", usages.get(0).getElement().getParent().getText());
    }

    @Test
    public void test_functor() {
        configureCode("A.ml", "module M<caret> () = struct module X = struct end end\n module X = M()");

        List<UsageInfo> usages = findUsages("A.ml");
        assertEquals("M()", usages.get(0).getElement().getParent().getText());
    }

    @Test
    public void test_variant() {
        configureCode("A.ml", "type t = | Red<caret>\n let color = Red");

        List<UsageInfo> usages = findUsages("A.ml");
        assertSize(1, usages);
        assertEquals("A.color", PsiTreeUtil.getParentOfType(usages.get(0).getElement(), RPsiLet.class).getQualifiedName());
    }
}
