package com.reason.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiObject;
import com.reason.lang.ocaml.OclParserDefinition;

public class LetParsingTest extends BaseParsingTestCase {
    public LetParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testConstant() {
        PsiLet let = first(parseCode("let x = 1").getLetExpressions());

        assertEquals("x", let.getName());
        assertFalse(let.isFunction());
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

        assertTrue(let.isFunction());
    }

    public void testObject() {
        PsiLet let = first(parseCode("let typeScaleRaw = { one = 1.375; two = 1.0 }").getLetExpressions());

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
        assertNotNull(PsiTreeUtil.findChildOfType(binding, PsiObject.class));
    }

}
