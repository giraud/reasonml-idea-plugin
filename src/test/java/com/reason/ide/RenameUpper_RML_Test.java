package com.reason.ide;

import org.junit.*;

public class RenameUpper_RML_Test extends ORBasePlatformTestCase {
    @Test
    public void rename_module_interface_declaration() {
        configureCode("A.re", """
                module A1 = { module type Intf<caret> = {}; };
                module Impl: A1.Intf = {};
                """);

        myFixture.renameElementAtCaret("NewIntf");

        myFixture.checkResult("""
                module A1 = { module type NewIntf = {}; };
                module Impl: A1.NewIntf = {};
                """);
    }

    @Test
    public void rename_module_declaration() {
        configureCode("A.re", """
                module A1<caret> = {};
                module A2 = A1;
                """);

        myFixture.renameElementAtCaret("B");

        myFixture.checkResult("""
                module B = {};
                module A2 = B;
                """);
    }

    @Test
    public void rename_functor_declaration() {
        configureCode("A.re", """
                module Make<caret> = (M:Def) => {};
                module A1 = Make({});
                """);

        myFixture.renameElementAtCaret("B");

        myFixture.checkResult("""
                module B = (M:Def) => {};
                module A1 = B({});
                """);
    }
}
