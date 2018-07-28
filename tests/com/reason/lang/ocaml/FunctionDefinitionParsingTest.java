package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiFunctionBody;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameters;

public class FunctionDefinitionParsingTest extends BaseParsingTestCase {
    public FunctionDefinitionParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testLetFunction() {
        PsiLet e = first(letExpressions(parseCode("let add x y = x + y")));

        assertTrue(e.isFunction());
        PsiFunction function = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertNotNull(function);
        assertEquals(2, first(PsiTreeUtil.findChildrenOfType(function, PsiParameters.class)).getArgumentsCount());
        assertNotNull(function.getBody());
    }

    public void testFunctionLetBinding() {
        PsiLet e = first(letExpressions(parseCode("let getAttributes node = let attr = \"r\" in attr")));

        assertTrue(e.isFunction());
        PsiFunction function = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertNotNull(function);
        assertEquals(1, first(PsiTreeUtil.findChildrenOfType(function, PsiParameters.class)).getArgumentsCount());
        assertNotNull(function.getBody());
    }

    public void testFunctionLetBinding2() {
        PsiFile file = parseCode("let visit_vo f = Printf.printf \"a\"; Printf.printf \"b\"");
        PsiLet e = first(letExpressions(file));

        assertTrue(e.isFunction());
        PsiFunction function = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertNotNull(function);
        assertEquals("Printf.printf \"a\"; Printf.printf \"b\"", function.getBody().getText());
    }

}
