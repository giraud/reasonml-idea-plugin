package com.reason.lang.reason;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;
import com.reason.BaseParsingTestCase;

public class CommentTest extends BaseParsingTestCase {
    public CommentTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testConstant() {
        PsiFile psiFile = parseCode("/* */");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
    }

    public void testConstant2() {
        PsiFile psiFile = parseCode("/* \"this is a string */\" */");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
        assertEquals(psiFile.getChildren().length, 1);
    }

}
