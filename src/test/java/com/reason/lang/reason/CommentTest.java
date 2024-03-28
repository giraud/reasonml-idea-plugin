package com.reason.lang.reason;

import com.intellij.psi.*;
import org.junit.*;

public class CommentTest extends RmlParsingTestCase {
    @Test
    public void test_constant() {
        PsiComment e = firstOfType(parseCode("/* */"), PsiComment.class);

        assertEquals("/* */", e.getText());
    }

    @Test
    public void test_constant_2() {
        PsiComment e = firstOfType(parseCode("/* \"this is a string */\" */"), PsiComment.class);

        assertEquals("/* \"this is a string */\" */", e.getText());
    }
}
