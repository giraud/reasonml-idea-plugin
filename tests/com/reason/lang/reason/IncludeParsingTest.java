package com.reason.lang.reason;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

public class IncludeParsingTest extends RmlParsingTestCase {
    public void test_one() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt;")));

        assertNull(PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class));
        assertEquals("Belt", e.getIncludePath());
    }

    public void test_functor() {
        PsiInclude e = firstOfType(parseCode("include Make({ type t; })"), PsiInclude.class);

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("Make", e.getIncludePath());
    }

    public void test_functor_with_path() {
        PsiInclude e = firstOfType(parseCode("include A.Make({ type t; })"), PsiInclude.class);

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("A.Make", e.getIncludePath());
    }
}
