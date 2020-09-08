package com.reason.lang.ocaml;

import java.util.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.reason.lang.BaseParsingTestCase;

public class ExpressionsParsingTest extends BaseParsingTestCase {
    public ExpressionsParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testA() {
        PsiFile file = parseCode("module Hooks = struct let a = fun (_, info as ei) -> x end\nlet b = 1");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(2, expressions.size());
    }

    public void testB() {
        PsiFile file = parseCode("let x = function | _ -> false\nlet y = 1");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(2, expressions.size());
    }
}
