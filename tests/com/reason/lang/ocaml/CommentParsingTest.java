package com.reason.lang.ocaml;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import org.junit.*;

public class CommentParsingTest extends BaseParsingTestCase {
    public CommentParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    @Test
    public void test_constant() {
        PsiFile psiFile = parseCode("(* *)");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
    }

    @Test
    public void test_constant2() {
        FileBase psiFile = parseCode("(* \"this is a string *)\" *)");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
        assertEquals(1, childrenCount(psiFile));
    }
}
