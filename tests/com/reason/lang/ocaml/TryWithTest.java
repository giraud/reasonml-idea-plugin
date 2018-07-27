package com.reason.lang.ocaml;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiTry;

public class TryWithTest extends BaseParsingTestCase {
    public TryWithTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testTry() {
        PsiTry try_ = (PsiTry) firstElement(parseCode("try f() with e -> let e = CErrors.push e"));
        assertEquals("e -> let e = CErrors.push e", try_.getWith().getText());
    }
}
