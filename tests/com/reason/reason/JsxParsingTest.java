package com.reason.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiTagStart;
import com.reason.lang.reason.RmlParserDefinition;

public class JsxParsingTest extends BaseParsingTestCase {
    public JsxParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testOptionAsTag() {
        // option here is not a ReasonML keyword
        PsiLet let = first(parseCode("let _ = <option className/>", true).getLetExpressions());

        PsiTagStart jsx = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertNotNull(jsx);
    }
}
