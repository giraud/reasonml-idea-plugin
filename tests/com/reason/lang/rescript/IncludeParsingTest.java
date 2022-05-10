package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

public class IncludeParsingTest extends ResParsingTestCase {
    public void test_one() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));

        assertNull(PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class));
        assertEquals("Belt", e.getIncludePath());
    }

    public void test_path() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));

        assertEquals("Belt.Array", e.getIncludePath());
    }

    public void test_functor() {
        PsiInclude e = first(includeExpressions(parseCode("include A.Make({ type t })")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getIncludePath());
    }

    public void test_chaining() {
        Collection<PsiInclude> includes = includeExpressions(parseCode("include Belt include Js"));

        assertSize(2, includes);
    }
}
