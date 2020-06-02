package com.reason.lang.reason;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;

public class CommentTest extends BaseParsingTestCase {
    public CommentTest() {
        super("", "re", new RmlParserDefinition());
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
