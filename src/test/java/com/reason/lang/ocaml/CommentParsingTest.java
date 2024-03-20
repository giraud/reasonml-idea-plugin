package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.reason.lang.*;
import org.junit.*;

public class CommentParsingTest extends BaseParsingTestCase {
    public CommentParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

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
}
