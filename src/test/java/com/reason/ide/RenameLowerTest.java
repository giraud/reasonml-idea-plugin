package com.reason.ide;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class RenameLowerTest extends ORBasePlatformTestCase {
    @Test
    public void test_RML_rename_let() {
        configureCode("A.re", "let x<caret> = 1; let z = x + 1;");
        myFixture.renameElementAtCaret("y");
        myFixture.checkResult("let y = 1; let z = y + 1;");
    }

    @Test
    public void test_NS_rename_let() {
        configureCode("A.res", "let x<caret> = 1\n let z = x + 1");
        myFixture.renameElementAtCaret("y");
        myFixture.checkResult("let y = 1\n let z = y + 1");
    }

    @Test
    public void test_OCL_rename_let() {
        configureCode("A.ml", "let x<caret> = 1\n let z = x + 1");
        myFixture.renameElementAtCaret("y");
        myFixture.checkResult("let y = 1\n let z = y + 1");
    }

    @Test
    public void test_RML_rename_type() {
        configureCode("A.re", "type x<caret>; type z = x;");
        myFixture.renameElementAtCaret("y");
        myFixture.checkResult("type y; type z = y;");
    }

    @Test
    public void test_NS_rename_type() {
        configureCode("A.res", "type x<caret>\n type z = x");
        myFixture.renameElementAtCaret("y");
        myFixture.checkResult("type y\n type z = y");
    }

    @Test
    public void test_OCL_rename_type() {
        configureCode("A.ml", "type x<caret>\n type z = x");
        myFixture.renameElementAtCaret("y");
        myFixture.checkResult("type y\n type z = y");
    }
}
