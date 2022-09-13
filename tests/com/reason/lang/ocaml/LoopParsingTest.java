package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class LoopParsingTest extends OclParsingTestCase {
    public void test_for() {
        PsiLetBinding e = firstOfType(parseCode("let _ = for i = 1 to pred l do unsafe_set rest i (f i) done"), PsiLet.class).getBinding();

        PsiForLoop l = PsiTreeUtil.findChildOfType(e, PsiForLoop.class);
        assertEquals("for i = 1 to pred l do unsafe_set rest i (f i) done", l.getText());
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/176
    public void test_GH_176() {
        PsiLet e = first(letExpressions(parseCode("let x = while true do match x with | _ -> () done")));
        PsiWhile while_ = (PsiWhile) e.getBinding().getFirstChild();

        assertEquals("true", while_.getCondition().getText());
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/189
    public void test_GH_189() {
        Collection<PsiLet> es = letExpressions(parseCode("let utf8_length s = while !p < len do () done; ()\nlet foo x = x"));
        PsiWhile while_ = (PsiWhile) ((PsiFunction) first(es).getBinding().getFirstChild()).getBody().getFirstChild();

        assertEquals("!p < len", while_.getCondition().getText());
        assertEquals("do () done", while_.getBody().getText());

        assertSize(2, es);
        assertEquals("foo", second(es).getName());
    }
}
