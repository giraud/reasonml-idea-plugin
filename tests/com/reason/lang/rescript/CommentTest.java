package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import org.junit.*;

public class CommentTest extends ResParsingTestCase {
    @Test
    public void test_constant() {
        FileBase psiFile = parseCode("/* */");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
    }

    @Test
    public void test_constant2() {
        FileBase psiFile = parseCode("/* \"this is a string */\" */");
        assertInstanceOf(firstElement(psiFile), PsiComment.class);
        assertEquals(1, childrenCount(psiFile));
    }
}
