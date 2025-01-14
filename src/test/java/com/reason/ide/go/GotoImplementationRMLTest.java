package com.reason.ide.go;

import org.junit.Test;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.CodeInsightTestUtil;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.RPsiQualifiedPathElement;

public class GotoImplementationRMLTest extends ORBasePlatformTestCase {
    @Test
    public void should_goto_implementation() {
        configureCode("A.rei", "let x: int;");
        configureCode("A.re", "let x = 1;");
        FileBase ref = configureCode("B.re", "let y = A.x<caret>;");

        PsiElement[] targets = CodeInsightTestUtil.gotoImplementation(myFixture.getEditor(), ref).targets;

        assertSize(1, targets);
        assertEquals("A.x", ((RPsiQualifiedPathElement) targets[0]).getQualifiedName());
        // TODO assertEquals("A.re", targets[0].getContainingFile().getName());
    }
}
