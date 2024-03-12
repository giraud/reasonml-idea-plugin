package com.reason.ide;

import org.junit.*;

public class RenameUpper_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void rename_module_interface_declaration() {
        configureCode("A.ml", """
                module A1 = struct module type Intf<caret> = sig end end
                module Impl: A1.Intf = struct end
                """);

        myFixture.renameElementAtCaret("NewIntf");

        myFixture.checkResult("""
                module A1 = struct module type NewIntf = sig end end
                module Impl: A1.NewIntf = struct end
                """);
    }

    @Test
    public void rename_module_declaration() {
        configureCode("A.ml", """
                module A1<caret> = struct end
                module A2 = A1
                """);

        myFixture.renameElementAtCaret("B");

        myFixture.checkResult("""
                module B = struct end
                module A2 = B
                """);
    }

    @Test
    public void rename_functor_declaration() {
        configureCode("A.ml", """
                module Make<caret> (M:Def) = struct end
                module A1 = Make(struct end)
                """);

        myFixture.renameElementAtCaret("B");

        myFixture.checkResult("""
                module B (M:Def) = struct end
                module A1 = B(struct end)
                """);
    }
}
