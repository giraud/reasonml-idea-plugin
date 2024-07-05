package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import org.junit.*;

public class CommentParsingTest extends OclParsingTestCase {
    @Test
    public void test_constant() {
        PsiComment e = firstOfType(parseCode("(* *)"), PsiComment.class);

        assertEquals("(* *)", e.getText());
    }

    @Test
    public void test_constant_2() {
        PsiComment e = firstOfType(parseCode("(* \"this is a string *)\" *)"), PsiComment.class);

        assertEquals("(* \"this is a string *)\" *)", e.getText());
    }


    // GH: https://github.com/giraud/reasonml-idea-plugin/issues/469
    @Test
    public void test_double_quotes() {
        FileBase e = parseCode("""
                let _ = '"' in
                (* '"' *)
                let _ = '"' in
                (* '"' *)
                """);

        PsiElement e0 = e.getFirstChild();
        assertInstanceOf(e0, RPsiLet.class);
        assertEquals("let _ = '\"'", e0.getText());
        PsiElement e1 = ORUtil.nextSibling(ORUtil.nextSibling(e0));
        assertInstanceOf(e1, PsiComment.class);
        assertEquals("(* '\"' *)", e1.getText());
        PsiElement e2 = ORUtil.nextSibling(e1);
        assertInstanceOf(e2, RPsiLet.class);
        assertEquals("let _ = '\"'", e2.getText());
        PsiElement e3 = ORUtil.nextSibling(ORUtil.nextSibling(e2));
        assertInstanceOf(e3, PsiComment.class);
        assertEquals("(* '\"' *)", e3.getText());
    }
}
