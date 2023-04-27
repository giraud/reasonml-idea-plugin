package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class LoopParsingTest extends OclParsingTestCase {
    @Test
    public void test_for() {
        RPsiLetBinding e = firstOfType(parseCode("let _ = for i = 1 to pred l do unsafe_set rest i (f i) done"), RPsiLet.class).getBinding();

        RPsiForLoop l = PsiTreeUtil.findChildOfType(e, RPsiForLoop.class);
        assertEquals("for i = 1 to pred l do unsafe_set rest i (f i) done", l.getText());
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/176
    @Test
    public void test_GH_176() {
        RPsiLet e = firstOfType(parseCode("let x = while true do match x with | _ -> () done"), RPsiLet.class);
        RPsiWhile while_ = (RPsiWhile) e.getBinding().getFirstChild();

        assertEquals("true", while_.getCondition().getText());
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/189
    @Test
    public void test_GH_189() {
        List<RPsiLet> es = ORUtil.findImmediateChildrenOfClass(parseCode("let utf8_length s = while !p < len do () done; ()\nlet foo x = x"), RPsiLet.class);
        RPsiWhile while_ = (RPsiWhile) ((RPsiFunction) es.get(0).getBinding().getFirstChild()).getBody().getFirstChild();

        assertEquals("!p < len", while_.getCondition().getText());
        assertEquals("do () done", while_.getBody().getText());

        assertSize(2, es);
        assertEquals("foo", second(es).getName());
    }
}
