package com.reason.lang.core;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiLet;
import org.junit.*;

import java.util.*;

public class FileBaseTest extends ORBasePlatformTestCase {
  @Test
  public void test_Rml_getQNameExpression() {
    FileBase f = configureCode("A.re", "module B = { let x = 1; }; let x = 2;");
    List<PsiLet> e = f.getQualifiedExpressions("A.B.x", PsiLet.class);

    assertSize(1, e);
    assertInstanceOf(e.iterator().next(), PsiLet.class);
  }
}
