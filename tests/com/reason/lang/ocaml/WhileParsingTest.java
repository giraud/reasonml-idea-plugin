package com.reason.lang.ocaml;

import com.intellij.psi.PsiWhileStatement;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

@SuppressWarnings("ConstantConditions")
public class WhileParsingTest extends BaseParsingTestCase {
    public WhileParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testIssue176() {
        PsiLet e = first(letExpressions(parseCode("let x = while true do match x with | _ -> () done")));
        PsiLetBinding binding = e.getBinding();

        assertInstanceOf(binding.getFirstChild(), PsiWhile.class);
    }

}
