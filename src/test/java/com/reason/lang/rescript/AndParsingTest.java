package com.reason.lang.rescript;

import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import org.junit.*;

import java.util.*;

public class AndParsingTest extends ResParsingTestCase {
    @Test
    public void test_let_chaining() {
        List<RPsiLet> lets = ORUtil.findImmediateChildrenOfClass(parseCode("let rec lx = x => x + 1 and ly = y => 3 + lx(y)"), RPsiLet.class);

        assertEquals(2, lets.size());
        assertEquals("lx", lets.get(0).getName());
        assertEquals("ly", lets.get(1).getName());
    }

    @Test
    public void test_module_chaining() {
        List<RPsiInnerModule> es = childrenOfType(parseCode("""
                module rec X: {} = {}
                and Y: {} = {}
                """), RPsiInnerModule.class);

        assertEquals(2, es.size());
        assertEquals("X", es.get(0).getName());
        assertEquals("Y", es.get(1).getName());
    }

    @Test
    public void test_module_type_chaining() {
        List<RPsiInnerModule> mods = childrenOfType(parseCode("""
                // in a .resi file
                module rec X : {}
                and Y : {}
                """), RPsiInnerModule.class);

        assertSize(2, mods);
        assertEquals("X", mods.get(0).getName());
        assertEquals("Y", mods.get(1).getName());
    }

    @Test
    public void test_type_chaining() {
        List<RPsiType> types = childrenOfType(parseCode("""
                type rec update = | NoUpdate
                and self<'state> = {state: 'state}
                """), RPsiType.class);

        assertEquals(2, types.size());
        assertEquals("update", first(types).getName());
        assertEquals("| NoUpdate", first(types).getBinding().getText());
        assertEquals("self", second(types).getName());
        assertEquals("{state: 'state}", second(types).getBinding().getText());
    }

    @Test
    public void test_type_chaining_lIdent() {
        List<RPsiType> types = childrenOfType(parseCode("""
                type rec t = y
                /* test */
                and y = string
                """), RPsiType.class);

        assertEquals(2, types.size());
        assertEquals("t", first(types).getName());
        assertEquals("y", second(types).getName());
    }
}
