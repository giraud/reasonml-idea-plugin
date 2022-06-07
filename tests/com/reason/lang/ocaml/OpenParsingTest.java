package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class OpenParsingTest extends OclParsingTestCase {
    public void test_one() {
        PsiOpen e = firstOfType(parseCode("open Belt"), PsiOpen.class);

        assertEquals("Belt", e.getPath());
    }

    public void test_path() {
        PsiOpen e = firstOfType(parseCode("open Belt.Array"), PsiOpen.class);

        assertEquals("Belt.Array", e.getPath());
    }

    public void test_chaining() {
        PsiOpen e = firstOfType(parseCode("open Belt Array"), PsiOpen.class);

        assertEquals("Belt", e.getPath());
    }

    public void test_functor() {
        PsiOpen e = firstOfType(parseCode("open Make(struct type t end)"), PsiOpen.class);

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("Make", e.getPath());
    }

    public void test_functor_with_path() {
        PsiOpen e = firstOfType(parseCode("open A.Make(struct type t end)"), PsiOpen.class);

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("A.Make", e.getPath());
    }
}
