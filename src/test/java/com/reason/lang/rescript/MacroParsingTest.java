package com.reason.lang.rescript;

import com.intellij.openapi.util.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class MacroParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        RPsiMacro e = firstOfType(parseCode("let _ = %raw(\"xxx\")"), RPsiMacro.class);

        assertEquals("%raw", e.getName());
        assertEquals("\"xxx\"", e.getContent().getText());
        assertEquals(new TextRange(1, e.getContent().getTextLength() - 1), e.getContent().getMacroTextRange()); // exclude "
    }

    @Test
    public void test_root_raw() {
        RPsiMacro e = firstOfType(parseCode("%%raw(\"import * from 'react'\")"), RPsiMacro.class);

        assertTrue(e.isRoot());
        assertEquals("%%raw", e.getName());
        assertEquals("\"import * from 'react'\"", e.getContent().getText());
        assertEquals(new TextRange(1, e.getContent().getTextLength() - 1), e.getContent().getMacroTextRange()); // exclude "
    }

    @Test
    public void test_multi_line() {
        RPsiMacro e = firstOfType(parseCode("""
                let _ = %raw(`
                    function (a) {
                    }
                `)
                """), RPsiMacro.class);

        assertEquals("%raw", e.getName());
        assertEquals("`\n    function (a) {\n    }\n`", e.getContent().getText());
        assertEquals(new TextRange(1, e.getContent().getTextLength() - 1), e.getContent().getMacroTextRange());  // exclude `
    }

    @Test
    public void test_GH_436() {
        RPsiMacro e = firstOfType(parseCode("""
                module GetOperatorBasesQuery = %graphql(`
                  query SchedGetOperatorBases {
                    operatorBases {
                      baseNid
                      name
                      utcOffset
                    }
                  }
                `)
                let x = 1
                """), RPsiMacro.class);

        assertEquals("%graphql", e.getName());
        assertEquals("`\n  query SchedGetOperatorBases {\n    operatorBases {\n      baseNid\n      name\n      utcOffset\n    }\n  }\n`", e.getContent().getText());
        assertEquals(new TextRange(1, e.getContent().getTextLength() - 1), e.getContent().getMacroTextRange());  // exclude `
    }


    /*
    % are called extension points
    list of extension points from rescript:
    %debugger
    %identity
    %%private
      module Calc = {
        %%private(let mult = (x, y) => x * y)
      }
    %raw
    %%raw
    %re
      let regex = %re("/^hello/")
    can be custom from ppx, like %graphql
     */
}
