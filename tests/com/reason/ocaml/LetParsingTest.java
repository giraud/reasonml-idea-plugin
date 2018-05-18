package com.reason.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.ocaml.OclParserDefinition;

public class LetParsingTest extends BaseParsingTestCase {
    public LetParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testConstant() {
        PsiLet let = first(parseCode("let x = 1").getLetExpressions());
        assertEquals("x", let.getName());
        assertFalse(let.isFunction());
    }

    public void testFunction() {
        PsiLet let = first(parseCode("let add x y = x + y").getLetExpressions());

        assertTrue(let.isFunction());
        PsiParameters params = first(PsiTreeUtil.findChildrenOfType(let, PsiParameters.class));
        assertEquals(2, params.getArgumentsCount());
        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
    }

    public void testFunctionLetBinding() {
        PsiLet let = first(parseCode("let getAttributes node = let attr = \"r\" in attr").getLetExpressions());

        assertTrue(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testLetBinding() {
        PsiLet let = first(parseCode("let obj = [%bs.obj { a = \"b\" }];").getLetExpressions());

        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testScopeWithSome() {
//  ?      PsiLet let = first(parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };").getLetExpressions());
//
//        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
//        Assert.assertNotNull(binding);
    }

    public void testScopeWithLIdent() {
        PsiLet let = first(parseCode("let l p = Js.log p; returnObj").getLetExpressions());

        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
        assertTrue(let.isFunction());
    }
}
