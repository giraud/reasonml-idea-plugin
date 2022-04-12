package com.reason.lang.ocaml;

import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class OpenParsingTest extends OclParsingTestCase {
    public void test_one() {
        PsiOpen e = first(openExpressions(parseCode("open Belt")));

        assertEquals("Belt", e.getPath());
    }

    public void test_path() {
        PsiOpen e = first(openExpressions(parseCode("open Belt.Array")));

        assertEquals("Belt.", ORUtil.findImmediateFirstChildOfClass(e, PsiPath.class).getText());
        assertEquals("Belt.Array", e.getPath());
    }

    public void test_chaining() {
        PsiOpen e = first(openExpressions(parseCode("open Belt Array")));

        assertEquals("Belt", e.getPath());
    }

    public void test_functor() {
        PsiOpen e = first(openExpressions(parseCode("open A.Make(struct type t end)")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getPath());
    }
}
