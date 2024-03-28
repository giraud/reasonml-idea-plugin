package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class LocalOpenParsingTest extends OclParsingTestCase {
    @Test
    public void test_local_open() {
        List<RPsiLet> lets = ORUtil.findImmediateChildrenOfClass(parseCode("let _ = Int64.(x + y / of_int 2) let x = 1"), RPsiLet.class);

        assertSize(2 , lets);
        RPsiLocalOpen localOpen = PsiTreeUtil.findChildOfType(lets.get(0).getBinding(), RPsiLocalOpen.class);
        assertEquals("(x + y / of_int 2)", localOpen.getText());
        assertFalse(lets.get(1).isFunction());
        assertEquals("1", lets.get(1).getBinding().getText());
    }

    @Test
    public void test_not_local_open() {
        PsiElement expression = parseCode("Js.log(\"nok\")").getFirstChild();
        assertFalse(expression instanceof RPsiLocalOpen);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/294
    @Test
    public void test_GH_294() {
        Collection<PsiNamedElement> es = expressions(parseCode("let _ = let open P in function | A -> true | _ -> false"));

        assertSize(1, es);
        RPsiLet e = (RPsiLet) es.iterator().next();
        assertEquals("let open P in function | A -> true | _ -> false", e.getBinding().getText());
        assertEquals("P", PsiTreeUtil.findChildOfType(e, RPsiOpen.class).getPath());
    }
}
