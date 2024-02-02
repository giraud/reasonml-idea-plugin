package com.reason.lang.core;

import com.google.common.collect.*;
import com.intellij.psi.util.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class ORUtilTest extends ORBasePlatformTestCase {
    @Test
    public void testModuleNameToFileNameWhenEmpty() {
        assertEquals("", ORUtil.moduleNameToFileName(""));
    }

    @Test
    public void testModuleNameToFileName() {
        assertEquals("testLower", ORUtil.moduleNameToFileName("TestLower"));
    }

    @Test
    public void testFileNameToModuleNameWhenEmpty() {
        assertEquals("", ORUtil.fileNameToModuleName(""));
        assertEquals("", ORUtil.fileNameToModuleName(".ml"));
    }

    @Test
    public void testFileNameToModuleName() {
        assertEquals("Lower", ORUtil.fileNameToModuleName("lower.ml"));
        assertEquals("Upper", ORUtil.fileNameToModuleName("Upper.ml"));
    }

    @Test
    public void test_Rml_letQualifiedPath() {
        FileBase f = configureCode("A.re", "let make = () => { let x = 1; }");
        RPsiLet e = ImmutableList.copyOf(PsiTreeUtil.findChildrenOfType(f, RPsiLet.class)).get(1);

        String qPath = Joiner.join(".", ORUtil.getQualifiedPath(e));

        assertEquals("A.make", qPath);
    }

    @Test
    public void test_Rml_letDestructuredQualifiedPath() {
        FileBase f = configureCode("A.re", "module M = { let make = () => { let (x, y) = other; }; }");
        RPsiLet letExpression = ImmutableList.copyOf(PsiTreeUtil.findChildrenOfType(f, RPsiLet.class)).get(1);

        String qualifiedPath = Joiner.join(".", ORUtil.getQualifiedPath(letExpression));

        assertEquals("A.M.make", qualifiedPath);
    }

    @Test
    public void test_in_interface_file() {
        FileBase intf = configureCode("A.resi", "module M = { let x: int }");
        FileBase impl = configureCode("A.res", "module M = { let x = 1 }");

        assertTrue(ORUtil.inInterface(PsiTreeUtil.findChildOfType(intf, RPsiLet.class)));
        assertFalse(ORUtil.inInterface(PsiTreeUtil.findChildOfType(impl, RPsiLet.class)));
    }

    @Test
    public void test_in_interface_module_type() {
        FileBase f = configureCode("A.res", "module type M = { let x: 1 }");

        assertTrue(ORUtil.inInterface(PsiTreeUtil.findChildOfType(f, RPsiLet.class)));
    }

    @Test
    public void test_in_interface_anonymous_module_type() {
        FileBase f = configureCode("A.res", "module M: { let x: int } = { let x = 1 }");
        ImmutableList<RPsiLet> es = ImmutableList.copyOf(PsiTreeUtil.findChildrenOfType(f, RPsiLet.class));

        assertTrue(ORUtil.inInterface(es.get(0)));
        assertFalse(ORUtil.inInterface(es.get(1)));
    }
}
