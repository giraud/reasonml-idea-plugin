package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiNamedElement;

import java.util.Collection;

public class ExpressionsParsingTest extends BaseParsingTestCase {
    public ExpressionsParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testA() {
        PsiFile file = parseCode("module Hooks = struct let a = fun (_, info as ei) -> x end\nlet b = 1", true);
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(2, expressions.size());
    }


}
