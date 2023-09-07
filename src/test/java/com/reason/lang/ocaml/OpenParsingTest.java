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
        RPsiOpen e = firstOfType(parseCode("open Belt"), RPsiOpen.class);

        assertNull(PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class));
        assertEquals("Belt", e.getPath());
        assertEquals("Belt", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_path() {
        RPsiOpen e = firstOfType(parseCode("open Belt.Array"), RPsiOpen.class);

        assertEquals("Belt.Array", e.getPath());
        assertEquals("Array", ORUtil.findImmediateLastChildOfType(e, myTypes.A_MODULE_NAME).getText());
    }

    @Test
    public void test_chaining() {
        RPsiOpen e = firstOfType(parseCode("open Belt Array"), RPsiOpen.class);

        assertEquals("Belt", e.getPath());
    }

    @Test
    public void test_functor() {
        RPsiOpen e = firstOfType(parseCode("open Make(struct type t end)"), RPsiOpen.class);

        assertTrue(e.useFunctor());
        RPsiFunctorCall c = PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class);
        assertEquals("Make", c.getName());
        assertEquals(myTypes.A_MODULE_NAME, c.getReferenceIdentifier().getNode().getElementType());
        assertEquals("Make", e.getPath());
    }

    @Test
    public void test_functor_with_path() {
        RPsiOpen e = firstOfType(parseCode("open A.Make(struct type t end)"), RPsiOpen.class);

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class).getName());
        assertEquals("A.Make", e.getPath());
    }
}
