package com.reason.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.reason.RmlParserDefinition;

public class LetParsingTest extends BaseParsingTestCase {
    public LetParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testConstant() {
        PsiLet let = first(parseCode("let x = 1;").getLetExpressions());
        assertEquals("x", let.getName());
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testFunctionLetBinding() {
        PsiLet let = first(parseCode("let getAttributes = node => { node; };").getLetExpressions());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testLetBinding() {
        PsiLet let = first(parseCode("let x = {\"u\": \"r\", \"l\": \"lr\"};").getLetExpressions());
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testLetBindingWithJsx() {
        PsiFileModuleImpl module = parseCode("let make = p => { render: x => { <div/>; } }");
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

    public void testLocalScope() {
        PsiLet let = first(parseCode("let x = { let y = 1; y + 3; }").getLetExpressions());

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
        assertNull(PsiTreeUtil.findChildOfType(binding, PsiRecord.class));
    }

    public void testRecord() {
        PsiLet let = first(parseCode("let typeScale = {one: 1.375, two: 1.0};").getLetExpressions());

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
        PsiRecord record = PsiTreeUtil.findChildOfType(binding, PsiRecord.class);
        assertNotNull(record);
        assertSize(2, record.getFields());
    }

}
