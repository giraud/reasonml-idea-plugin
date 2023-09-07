package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class OpenParsingTest extends ResParsingTestCase {
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
    public void test_functor() {
        RPsiOpen e = first(openExpressions(parseCode("open Make({ type t })")));

        assertTrue(e.useFunctor());
        RPsiFunctorCall c = PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class);
        assertEquals("Make", c.getName());
        assertEquals(myTypes.A_MODULE_NAME, c.getReferenceIdentifier().getNode().getElementType());
        assertEquals("Make", e.getPath());
    }

    @Test
    public void test_functor_with_path() {
        RPsiOpen e = first(openExpressions(parseCode("open A.Make({ type t })")));

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, RPsiFunctorCall.class).getName());
        assertEquals("A.Make", e.getPath());
    }

    @Test
    public void test_many() {
        List<RPsiOpen> es = openExpressions(parseCode("open Belt\n open Css"));

        assertSize(2, es);
        assertEquals("Belt", es.get(0).getPath());
        assertEquals("Css", es.get(1).getPath());
    }

    @Test
    public void test_many_paths() {
        List<RPsiOpen> es = openExpressions(parseCode("open Belt.Array\n open Css.Types"));

        assertEquals("Belt.Array", es.get(0).getPath());
        assertEquals("Css.Types", es.get(1).getPath());
    }

    @Test
    public void test_chaining() {
        RPsiOpen e = firstOfType(parseCode("open Css.Rules\n fontStyle"), RPsiOpen.class);

        assertEquals("Css.Rules", e.getPath());
    }
}
