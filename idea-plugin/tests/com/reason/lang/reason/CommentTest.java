package com.reason.lang.reason;

import com.intellij.psi.PsiComment;
import com.reason.ide.files.FileBase;

public class CommentTest extends RmlParsingTestCase {
    public void test_constant() {
        FileBase psiFile = parseCode("/* */");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
    }

    public void test_constant2() {
        FileBase psiFile = parseCode("/* \"this is a string */\" */");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
        assertEquals(1, childrenCount(psiFile));
    }
}
