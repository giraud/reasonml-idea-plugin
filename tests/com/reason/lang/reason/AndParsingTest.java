package com.reason.lang.reason;

import com.reason.lang.core.psi.*;
import org.junit.*;

import java.util.*;

public class AndParsingTest extends RmlParsingTestCase {
    @Test
    public void test_let_chaining() {
        List<RPsiLet> lets = letExpressions(parseCode("let rec lx = x => x + 1 and ly = y => 3 + lx(y)"));

        assertEquals(2, lets.size());
        assertEquals("lx", lets.get(0).getName());
        assertEquals("ly", lets.get(1).getName());
    }

    @Test
    public void test_module_chaining() {
        List<RPsiModule> mods = moduleExpressions(parseCode("module rec X: {} = {} and Y: {} = {};"));

        assertEquals(2, mods.size());
        assertEquals("X", mods.get(0).getName());
        assertEquals("Y", mods.get(1).getName());
    }

    /* type update = | NoUpdate and 'state self = {state: 'state;}*/
    @Test
    public void test_and() {
        List<RPsiType> types = typeExpressions(parseCode("type update = | NoUpdate and self('state) = {state: 'state};"));

        assertEquals(2, types.size());
        assertEquals("update", first(types).getName());
        assertEquals("self", second(types).getName());
    }
}
