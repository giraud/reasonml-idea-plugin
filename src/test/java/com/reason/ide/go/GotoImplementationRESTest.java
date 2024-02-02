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
public class GotoImplementationRESTest extends ORBasePlatformTestCase {
    @Test
    public void should_goto_implementation() {
        configureCode("A.resi", "let x: int");
        configureCode("A.res", "let x = 1");
        FileBase ref = configureCode("B.res", "let y = A.x<caret>");

        PsiElement[] targets = CodeInsightTestUtil.gotoImplementation(myFixture.getEditor(), ref).targets;
        assertSize(1, targets);
        assertEquals("A.x", ((RPsiQualifiedPathElement) targets[0]).getQualifiedName());
        assertEquals("A.res", targets[0].getContainingFile().getName());
    }
}
