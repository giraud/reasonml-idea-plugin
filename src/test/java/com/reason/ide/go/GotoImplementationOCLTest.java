package com.reason.ide.go;

import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class GotoImplementationOCLTest extends ORBasePlatformTestCase {
    @Test
    public void should_goto_implementation() {
        configureCode("A.mli", "val x: int");
        configureCode("A.ml", "let x = 1");
        FileBase ref = configureCode("B.ml", "let y = A.x<caret>");

        PsiElement[] targets = CodeInsightTestUtil.gotoImplementation(myFixture.getEditor(), ref).targets;
        assertSize(1, targets);
        assertEquals("A.x", ((RPsiQualifiedPathElement) targets[0]).getQualifiedName());
        assertEquals("A.ml", targets[0].getContainingFile().getName());
    }
}
