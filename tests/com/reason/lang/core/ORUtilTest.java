package com.reason.lang.core;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiLet;

public class ORUtilTest extends ORBasePlatformTestCase {

    public void testModuleNameToFileNameWhenEmpty() {
        assertEquals("", ORUtil.moduleNameToFileName(""));
    }

    public void testModuleNameToFileName() {
        assertEquals("testLower", ORUtil.moduleNameToFileName("TestLower"));
    }

    public void testFileNameToModuleNameWhenEmpty() {
        assertEquals("", ORUtil.fileNameToModuleName(""));
        assertEquals("", ORUtil.fileNameToModuleName(".ml"));
    }

    public void testFileNameToModuleName() {
        assertEquals("Lower", ORUtil.fileNameToModuleName("lower.ml"));
        assertEquals("Upper", ORUtil.fileNameToModuleName("Upper.ml"));
    }

    public void testLetQualifiedName() {
        configureCode("A.re", "let make = () => { let x<caret> = 1; }");

        String qPath = ORUtil.getQualifiedPath((PsiNameIdentifierOwner) myFixture.getElementAtCaret());
        assertEquals("A.make", qPath);
    }

    public void testLetDestructuredQualifiedName() {
        FileBase f = configureCode("A.re", "module M = { let make = () => { let (x, y) = other; }; }");
        PsiLet letExpression = f.getLetExpression("(x, y)");
        String qualifiedPath = ORUtil.getQualifiedPath(letExpression);
        assertEquals("A.M.make", qualifiedPath);
    }
}