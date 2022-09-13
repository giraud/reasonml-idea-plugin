package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class OpenParsingTest extends OclParsingTestCase {
    @Test
    public void test_one() {
        PsiOpen e = firstOfType(parseCode("open Belt"), PsiOpen.class);

        assertNull(PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class));
        assertEquals("Belt", e.getPath());
        assertEquals("Belt", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_path() {
        PsiOpen e = firstOfType(parseCode("open Belt.Array"), PsiOpen.class);

        assertEquals("Belt.Array", e.getPath());
        assertEquals("Array", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_chaining() {
        PsiOpen e = firstOfType(parseCode("open Belt Array"), PsiOpen.class);

        assertEquals("Belt", e.getPath());
    }

    @Test
    public void test_functor() {
        PsiOpen e = firstOfType(parseCode("open Make(struct type t end)"), PsiOpen.class);

        assertTrue(e.useFunctor());
        PsiFunctorCall c = PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class);
        assertEquals("Make", c.getName());
        assertEquals(myTypes.A_MODULE_NAME, c.getNavigationElement().getNode().getElementType());
        assertEquals("Make", e.getPath());
    }

    @Test
    public void test_functor_with_path() {
        PsiOpen e = firstOfType(parseCode("open A.Make(struct type t end)"), PsiOpen.class);

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("A.Make", e.getPath());
    }
}
