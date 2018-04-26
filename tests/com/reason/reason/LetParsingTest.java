package com.reason.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.reason.RmlParserDefinition;
import junit.framework.Assert;

public class LetParsingTest extends BaseParsingTestCase {
    public LetParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testConstant() {
        PsiLet let = first(parseCode("let x = 1;").getLetExpressions());
        Assert.assertEquals("x", let.getName());
    }

    public void testFunction() {
        PsiLet let = first(parseCode("let add = (x,y) => x + y;").getLetExpressions());

        PsiParameters params = first(PsiTreeUtil.findChildrenOfType(let, PsiParameters.class));
        Assert.assertEquals(2, params.getArgumentsCount());
        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
    }

    public void testFunctionLetBinding() {
        PsiLet let = first(parseCode("let getAttributes = node => { node; };").getLetExpressions());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testLetBinding() {
        PsiLet let = first(parseCode("let x = {\"u\": \"r\", \"l\": \"lr\"};").getLetExpressions());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testLetBindingWithJsx() {
        // params !!
        //PsiLet let = first(parseCode("let renderGraphic = r => <div/>;").getLetExpressions());
        //assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
        PsiFileModuleImpl module = parseCode("let make = (p) => { render: x => { <div/>; } }");
        PsiElement[] children = module.getChildren();
        PsiElement element = PsiTreeUtil.nextLeaf(children[1], true);
        assertNull(element);
        assertSize(1, module.getLetExpressions());
    }

    public void testScopeWithSome() {
        PsiLet let = first(parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };").getLetExpressions());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testScopeWithLIdent() {
        PsiLet let = first(parseCode("let l = (p) => { Js.log(p); returnObj; };").getLetExpressions());

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
    }
}
