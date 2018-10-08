package com.reason.lang.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PsiUtilTest {

    @Test
    public void moduleNameToFileNameWhenEmpty() {
        assertEquals("", ORUtil.moduleNameToFileName(""));
    }

    @Test
    public void moduleNameToFileName() {
        assertEquals("testLower", ORUtil.moduleNameToFileName("TestLower"));
    }

    @Test
    public void fileNameToModuleNameWhenEmpty() {
        assertEquals("", ORUtil.fileNameToModuleName(""));
        assertEquals("", ORUtil.fileNameToModuleName(".ml"));
    }

    @Test
    public void fileNameToModuleName() {
        assertEquals("Lower", ORUtil.fileNameToModuleName("lower.ml"));
        assertEquals("Upper", ORUtil.fileNameToModuleName("Upper.ml"));
    }
}