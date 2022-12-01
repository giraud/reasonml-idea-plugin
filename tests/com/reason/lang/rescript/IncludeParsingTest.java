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
        RPsiInclude e = first(includeExpressions(parseCode("include Belt")));

        assertNull(PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class));
        assertEquals("Belt", e.getIncludePath());
        assertEquals("Belt", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_path() {
        RPsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));

        assertEquals("Belt.Array", e.getIncludePath());
        assertEquals("Array", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_functor() {
        RPsiInclude e = firstOfType(parseCode("include Make({ type t })"), RPsiInclude.class);

        assertTrue(e.useFunctor());
        RPsiFunctorCall c = PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class);
        assertEquals(myTypes.A_MODULE_NAME, c.getNavigationElement().getNode().getElementType());
        assertEquals("Make", c.getName());
        assertEquals("Make", e.getIncludePath());
    }

    @Test
    public void test_functor_with_path() {
        RPsiInclude e = firstOfType(parseCode("include A.Make({ type t })"), RPsiInclude.class);

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class).getName());
        assertEquals("A.Make", e.getIncludePath());
    }

    @Test
    public void test_chaining() {
        Collection<RPsiInclude> includes = includeExpressions(parseCode("include Belt include Js"));

        assertSize(2, includes);
    }
}
