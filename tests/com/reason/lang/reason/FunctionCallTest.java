package com.reason.lang.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.psi.PsiLet;

public class FunctionCallTest extends BaseParsingTestCase {
    public FunctionCallTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testCall() {
        PsiLet e = first(letExpressions(parseCode("let t = string_of_int(1);")));

        assertNotNull(first(PsiTreeUtil.findChildrenOfType(e, PsiFunctionCallParams.class)));
    }

}
