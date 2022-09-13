package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class LocalOpenParsingTest extends OclParsingTestCase {
    public void test_local_open() {
        List<PsiLet> lets = letExpressions(parseCode("let _ = Int64.(x + y / of_int 2) let x = 1"));

        assertSize(2 , lets);
        PsiLocalOpen localOpen = PsiTreeUtil.findChildOfType(lets.get(0).getBinding(), PsiLocalOpen.class);
        assertEquals("(x + y / of_int 2)", localOpen.getText());
        assertFalse(lets.get(1).isFunction());
        assertEquals("1", lets.get(1).getBinding().getText());
    }

    public void test_not_local_open() {
        PsiElement expression = firstElement(parseCode("Js.log(\"nok\")"));
        assertFalse(expression instanceof PsiLocalOpen);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/294
    public void test_GH_294() {
        Collection<PsiNamedElement> es = expressions(parseCode("let _ = let open P in function | A -> true | _ -> false"));

        assertSize(1, es);
        PsiLet e = (PsiLet) es.iterator().next();
        assertEquals("let open P in function | A -> true | _ -> false", e.getBinding().getText());
        assertEquals("P", PsiTreeUtil.findChildOfType(e, PsiOpen.class).getPath());
    }
}
