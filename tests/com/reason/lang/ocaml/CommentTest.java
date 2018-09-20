package com.reason.lang.ocaml;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;
import com.reason.lang.BaseParsingTestCase;

@SuppressWarnings("ConstantConditions")
public class CommentTest extends BaseParsingTestCase {
    public CommentTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testConstant() {
        PsiFile psiFile = parseCode("(* *)");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
    }

    public void testConstant2() {
        PsiFile psiFile = parseCode("(* \"this is a string *)\" *)");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
        assertEquals(psiFile.getChildren().length, 1);
    }

}
