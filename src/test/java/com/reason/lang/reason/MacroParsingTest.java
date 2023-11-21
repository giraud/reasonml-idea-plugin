package com.reason.lang.reason;

import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class MacroParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        RPsiLet expression = firstOfType(parseCode("let _ = [%raw \"xxx\"]"), RPsiLet.class);

        PsiElement macro = expression.getBinding().getFirstChild();
        assertInstanceOf(macro, RPsiMacro.class);

        RPsiMacroBody rawMacroBody = PsiTreeUtil.findChildOfType(macro, RPsiMacroBody.class);
        assertEquals("%raw", PsiTreeUtil.findChildOfType(macro, RPsiMacroName.class).getText());
        assertEquals("\"xxx\"", rawMacroBody.getText());
        assertEquals(new TextRange(1, 4), rawMacroBody.getMacroTextRange());
    }

    @Test
    public void test_rootRaw() {
        RPsiMacro e = firstOfType(parseCode("%raw \"xxx\";"), RPsiMacro.class);

        assertTrue(e.isRoot());
        assertEquals("\"xxx\"", e.getContent().getText());
    }

    @Test
    public void test_multiLine() {
        RPsiLet expression = firstOfType(parseCode("let _ = [%raw {|function (a) {}|}]"), RPsiLet.class);

        RPsiMacro macro = (RPsiMacro) expression.getBinding().getFirstChild();

        assertEquals("%raw", macro.getName());
        assertEquals("{|function (a) {}|}", macro.getContent().getText());
        assertEquals(new TextRange(2, 17), macro.getContent().getMacroTextRange()); // exclude {| |}
    }


    @Test
    public void test_GH_xxx() {
        RPsiMacro e = firstOfType(parseCode("""
                module GetOperatorBasesQuery = [%graphql {|
                  query SchedGetOperatorBases {
                    operatorBases {
                      baseNid
                      name
                      utcOffset
                    }
                  }
                |}]
                let x = 1
                """), RPsiMacro.class);

        assertEquals("%graphql", e.getName());
        assertEquals("{|\n  query SchedGetOperatorBases {\n    operatorBases {\n      baseNid\n      name\n      utcOffset\n    }\n  }\n|}", e.getContent().getText());
    }
}
