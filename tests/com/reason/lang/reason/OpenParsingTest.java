package com.reason.lang.reason;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class OpenParsingTest extends RmlParsingTestCase {
    @Test
    public void test_one() {
        PsiOpen e = firstOfType(parseCode("open Belt;"), PsiOpen.class);

        assertNull(PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class));
        assertEquals("Belt", e.getPath());
        assertEquals("Belt", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_path() {
        PsiOpen e = firstOfType(parseCode("open Belt.Array;"), PsiOpen.class);

        assertEquals("Belt.Array", e.getPath());
        assertEquals("Array", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_functor() {
        PsiOpen e = first(openExpressions(parseCode("open Make({ type t; })")));

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("Make", e.getPath());
    }

    @Test
    public void test_functor_with_path() {
        PsiOpen e = first(openExpressions(parseCode("open A.Make({ type t; })")));

        assertTrue(e.useFunctor());
        PsiFunctorCall c = PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class);
        assertEquals("Make", c.getName());
        assertEquals(myTypes.A_MODULE_NAME, c.getNavigationElement().getNode().getElementType());
        assertEquals("A.Make", e.getPath());
    }
}
