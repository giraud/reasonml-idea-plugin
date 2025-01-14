package com.reason.ide.go;

import com.intellij.psi.*;
import com.intellij.testFramework.fixtures.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import org.junit.*;

public class GotoImplementationRESTest extends ORBasePlatformTestCase {
    @Test
    public void should_goto_implementation() {
        configureCode("A.resi", "let x: int");
        configureCode("A.res", "let x = 1");
        FileBase b = configureCode("B.res", "let y = A.x<caret>");

        PsiElement[] targets = CodeInsightTestUtil.gotoImplementation(myFixture.getEditor(), b).targets;

        assertSize(1, targets);
        assertEquals("A.x", ((RPsiQualifiedPathElement) targets[0]).getQualifiedName());
        // TODO assertEquals("A.res", targets[0].getContainingFile().getName());
    }
}
