package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class IncludeParsingTest extends ResParsingTestCase {
    @Test
    public void test_one() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));

        assertNull(PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class));
        assertEquals("Belt", e.getIncludePath());
        assertEquals("Belt", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_path() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));

        assertEquals("Belt.Array", e.getIncludePath());
        assertEquals("Array", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_functor() {
        PsiInclude e = firstOfType(parseCode("include Make({ type t })"), PsiInclude.class);

        assertTrue(e.useFunctor());
        PsiFunctorCall c = PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class);
        assertEquals(myTypes.A_MODULE_NAME, c.getNavigationElement().getNode().getElementType());
        assertEquals("Make", c.getName());
        assertEquals("Make", e.getIncludePath());
    }

    @Test
    public void test_functor_with_path() {
        PsiInclude e = firstOfType(parseCode("include A.Make({ type t })"), PsiInclude.class);

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("A.Make", e.getIncludePath());
    }

    @Test
    public void test_chaining() {
        Collection<PsiInclude> includes = includeExpressions(parseCode("include Belt include Js"));

        assertSize(2, includes);
    }
}
