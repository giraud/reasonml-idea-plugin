package com.reason.lang.ocaml;

import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import org.junit.*;

import java.util.*;

public class OperatorParsingTest extends OclParsingTestCase {
    // https://github.com/giraud/reasonml-idea-plugin/issues/314
    @Test
    public void test_GH_314_structural_difference() {
        List<RPsiLet> lets = ORUtil.findImmediateChildrenOfClass(parseCode("let is_traced () = !debug <> DebugOff\n let name_vfun () = ()"), RPsiLet.class);

        assertSize(2, lets);
        assertEquals("is_traced", lets.get(0).getName());
        assertEquals("name_vfun", lets.get(1).getName());
    }
}
