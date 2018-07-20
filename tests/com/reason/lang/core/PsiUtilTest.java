package com.reason.lang.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PsiUtilTest {

    @Test
    public void moduleNameToFileNameWhenEmpty() {
        assertEquals("", PsiUtil.moduleNameToFileName(""));
    }

    @Test
    public void moduleNameToFileName() {
        assertEquals("testLower", PsiUtil.moduleNameToFileName("TestLower"));
    }

    @Test
    public void fileNameToModuleNameWhenEmpty() {
        assertEquals("", PsiUtil.fileNameToModuleName(""));
        assertEquals("", PsiUtil.fileNameToModuleName(".ml"));
    }

    @Test
    public void fileNameToModuleName() {
        assertEquals("Lower", PsiUtil.fileNameToModuleName("lower.ml"));
        assertEquals("Upper", PsiUtil.fileNameToModuleName("Upper.ml"));
    }
}