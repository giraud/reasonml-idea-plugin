package com.reason.ide.go;

import org.junit.Test;
import com.intellij.codeInsight.navigation.GotoTargetHandler;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.CodeInsightTestUtil;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.RPsiQualifiedPathElement;

public class GotoImplementationRESTest extends ORBasePlatformTestCase {
    @Test
    public void should_goto_implementation() {
        configureCode("A.resi", "let x: int");
        configureCode("A.res", "let x = 1");
        FileBase b = configureCode("B.res", "let y = A.x<caret>");

        myFixture.openFileInEditor(b.getVirtualFile());
        GotoTargetHandler.GotoData gotoData = CodeInsightTestUtil.gotoImplementation(myFixture.getEditor(), b);

        PsiElement[] targets = gotoData.targets;
        assertSize(1, targets);
        assertEquals("A.x", ((RPsiQualifiedPathElement) targets[0]).getQualifiedName());
        assertEquals("A.res", targets[0].getContainingFile().getName());
    }
}
