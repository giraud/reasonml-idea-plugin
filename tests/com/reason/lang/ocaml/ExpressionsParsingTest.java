package com.reason.lang.ocaml;

import com.intellij.psi.*;
import org.junit.*;

import java.util.*;

public class ExpressionsParsingTest extends OclParsingTestCase {
    @Test
    public void testA() {
        PsiFile file = parseCode("module Hooks = struct let a = fun (_, info as ei) -> x end\nlet b = 1");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(2, expressions.size());
    }

    @Test
    public void testB() {
        PsiFile file = parseCode("let x = function | _ -> false\nlet y = 1");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(2, expressions.size());
    }
}
