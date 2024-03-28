package com.reason.lang.dune;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import org.junit.*;

public class CommentParsingTest extends DuneParsingTestCase {
    @Test
    public void test_single_comment() {
        DuneFile e = parseDuneCode("; duplicate module names in the whole build.");
        assertInstanceOf(e.getFirstChild(), PsiComment.class);
        assertEquals("; duplicate module names in the whole build.", e.getFirstChild().getText());
    }
}
