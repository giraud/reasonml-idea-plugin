package com.reason.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.ocaml.OclParserDefinition;
import junit.framework.Assert;

public class LetParsingTest extends BaseParsingTestCase {
    public LetParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testConstant() {
        PsiLet let = first(parseCode("let x = 1").getLetExpressions());
        Assert.assertEquals("x", let.getName());
    }

    public void testFunction() {
        PsiLet let = first(parseCode("let add x y = x + y").getLetExpressions());

        PsiParameters params = first(PsiTreeUtil.findChildrenOfType(let, PsiParameters.class));
        Assert.assertEquals(2, params.getArgumentsCount());
        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        Assert.assertNotNull(binding);
    }

    public void testScopeWithSome() {
//  ?      PsiLet let = first(parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };").getLetExpressions());
//
//        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
//        Assert.assertNotNull(binding);
    }

    public void testScopeWithLIdent() {
        PsiLet let = first(parseCode("let l p = Js.log p; returnObj").getLetExpressions());

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        Assert.assertNotNull(binding);
    }
}
