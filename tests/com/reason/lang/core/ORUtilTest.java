package com.reason.lang.core;

import com.intellij.psi.util.*;
import com.reason.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

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

    public void test_Rml_letQualifiedPath() {
        FileBase f = configureCode("A.re", "let make = () => { let x = 1; }");
        PsiLet e = PsiTreeUtil.findChildOfType(f, PsiFakeModule.class).getLetExpression("x");

        String qPath = Joiner.join(".", ORUtil.getQualifiedPath(e));
        assertEquals("A.make", qPath);
    }

    public void test_Rml_letDestructuredQualifiedPath() {
        FileBase f = configureCode("A.re", "module M = { let make = () => { let (x, y) = other; }; }");
        PsiLet letExpression = PsiTreeUtil.findChildOfType(f, PsiFakeModule.class).getLetExpression("(x, y)");
        String qualifiedPath = Joiner.join(".", ORUtil.getQualifiedPath(letExpression));
        assertEquals("A.M.make", qualifiedPath);
    }
}
