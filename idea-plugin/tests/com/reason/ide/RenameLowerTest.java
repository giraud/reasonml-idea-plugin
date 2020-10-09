package com.reason.ide;

public class RenameLowerTest extends ORBasePlatformTestCase {
  public void test_RML_rename_let() {
    configureCode("A.re", "let x<caret> = 1; let z = x + 1;");
    myFixture.renameElementAtCaret("y");
    myFixture.checkResult("let y = 1; let z = y + 1;");
  }

  public void test_NS_rename_let() {
    configureCode("A.res", "let x<caret> = 1\n let z = x + 1");
    myFixture.renameElementAtCaret("y");
    myFixture.checkResult("let y = 1\n let z = y + 1");
  }

  public void test_OCL_rename_let() {
    configureCode("A.ml", "let x<caret> = 1\n let z = x + 1");
    myFixture.renameElementAtCaret("y");
    myFixture.checkResult("let y = 1\n let z = y + 1");
  }

  public void test_RML_rename_type() {
    configureCode("A.re", "type x<caret>; type z = x;");
    myFixture.renameElementAtCaret("y");
    myFixture.checkResult("type y; type z = y;");
  }

  public void test_NS_rename_type() {
    configureCode("A.res", "type x<caret>\n type z = x");
    myFixture.renameElementAtCaret("y");
    myFixture.checkResult("type y\n type z = y");
  }

  public void test_OCL_rename_type() {
    configureCode("A.ml", "type x<caret>\n type z = x");
    myFixture.renameElementAtCaret("y");
    myFixture.checkResult("type y\n type z = y");
  }
}
