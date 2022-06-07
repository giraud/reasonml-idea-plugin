package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class OpenParsingTest extends ResParsingTestCase {
    public void test_one() {
        PsiOpen e = firstOfType(parseCode("open Belt"), PsiOpen.class);

        assertNull(PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class));
        assertEquals("Belt", e.getPath());
    }

    public void test_path() {
        PsiOpen e = firstOfType(parseCode("open Belt.Array"), PsiOpen.class);

        assertEquals("Belt.Array", e.getPath());
    }

    public void test_functor() {
        PsiOpen e = first(openExpressions(parseCode("open Make({ type t })")));

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("Make", e.getPath());
    }

    public void test_functor_with_path() {
        PsiOpen e = first(openExpressions(parseCode("open A.Make({ type t })")));

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("A.Make", e.getPath());
    }

    public void test_many() {
        List<PsiOpen> es = openExpressions(parseCode("open Belt\n open Css"));

        assertSize(2, es);
        assertEquals("Belt", es.get(0).getPath());
        assertEquals("Css", es.get(1).getPath());
    }

    public void test_many_paths() {
        List<PsiOpen> es = openExpressions(parseCode("open Belt.Array\n open Css.Types"));

        assertEquals("Belt.Array", es.get(0).getPath());
        assertEquals("Css.Types", es.get(1).getPath());
    }

    public void test_chaining() {
        PsiOpen e = firstOfType(parseCode("open Css.Rules\n fontStyle"), PsiOpen.class);

        assertEquals("Css.Rules", e.getPath());
    }
}
