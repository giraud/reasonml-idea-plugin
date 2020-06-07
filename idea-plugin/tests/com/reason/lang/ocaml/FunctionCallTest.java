package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.reason.RmlParserDefinition;

public class FunctionCallTest extends BaseParsingTestCase {
    public FunctionCallTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testCall() {
        //PsiLet e = first(letExpressions(parseCode("let t = string_of_int 1")));
        //
        //assertNotNull(first(PsiTreeUtil.findChildrenOfType(e, PsiFunctionCallParams.class)));
    }

}
