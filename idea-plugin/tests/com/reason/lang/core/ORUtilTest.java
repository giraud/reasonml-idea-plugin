package com.reason.lang.core;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiFakeModule;
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

  public void test_Rml_letQualifiedPath() {
    FileBase f = configureCode("A.re", "let make = () => { let x = 1; }");
    PsiLet e = PsiTreeUtil.findChildOfType(f, PsiFakeModule.class).getLetExpression("x");

    String qPath = ORUtil.getQualifiedPath(e);
    assertEquals("A.make", qPath);
  }

  public void test_Rml_letDestructuredQualifiedPath() {
    FileBase f = configureCode("A.re", "module M = { let make = () => { let (x, y) = other; }; }");
    PsiLet letExpression =
        PsiTreeUtil.findChildOfType(f, PsiFakeModule.class).getLetExpression("(x, y)");
    String qualifiedPath = ORUtil.getQualifiedPath(letExpression);
    assertEquals("A.M.make", qualifiedPath);
  }
}
