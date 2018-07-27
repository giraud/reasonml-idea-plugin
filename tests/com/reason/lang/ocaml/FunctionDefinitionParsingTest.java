package com.reason.lang.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunction;
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
    //
    //public void testAnonFunction() {
    //    PsiLet e = first(parseCode("let x = Belt.map items (fun item -> item)").getLetExpressions());
    //
    //    PsiFunction function = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunction.class);
    //    assertNotNull(function);
    //    assertEquals(1, first(PsiTreeUtil.findChildrenOfType(function, PsiParameters.class)).getArgumentsCount());
    //}
    //
    //public void testInnerFunction() {
    //    PsiLet e = first(parseCode("let x error = Belt.Array.mapU errors (fun error  -> error##message)").getLetExpressions());
    //
    //    PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
    //    assertNotNull(function.getBody());
    //}

}
