package com.reason.lang.napkin;

import com.intellij.psi.PsiComment;
import com.reason.ide.files.FileBase;

public class CommentTest extends NsParsingTestCase {
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
