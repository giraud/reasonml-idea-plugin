package com.reason.lang.napkin;

import com.intellij.psi.PsiComment;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;

public class CommentTest extends BaseParsingTestCase {
    public CommentTest() {
        super("", "res", new NsParserDefinition());
    }

    public void testConstant() {
        FileBase psiFile = parseCode("/* */");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
    }

    public void testConstant2() {
        FileBase psiFile = parseCode("/* \"this is a string */\" */");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
        assertEquals(1, childrenCount(psiFile));
    }
}
