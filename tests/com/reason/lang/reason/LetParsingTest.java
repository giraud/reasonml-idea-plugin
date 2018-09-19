package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiRecord;

public class LetParsingTest extends BaseParsingTestCase {
    public LetParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testConstant() {
        PsiLet let = first(letExpressions(parseCode("let x = 1;")));
        assertEquals("x", let.getName());
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testFunctionLetBinding() {
        PsiLet let = first(letExpressions(parseCode("let getAttributes = node => { node; };")));
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testLetBinding() {
        PsiLet let = first(letExpressions(parseCode("let x = {\"u\": \"r\", \"l\": \"lr\"};")));
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testLetBindingWithJsx() {
        PsiFile file = parseCode("let make = p => { render: x => { <div/>; } }");
        PsiElement[] children = file.getChildren();
        PsiElement element = PsiTreeUtil.nextLeaf(children[0], true);

        assertNull(element);
        assertSize(1, expressions(file));
    }

    public void testScopeWithSome() {
        PsiLet let = first(letExpressions(parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };")));
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testScopeWithLIdent() {
        PsiLet let = first(letExpressions(parseCode("let l = (p) => { Js.log(p); returnObj; };")));

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
    }

    public void testLocalScope() {
        PsiLet let = first(letExpressions(parseCode("let x = { let y = 1; y + 3; }")));

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
        assertNull(PsiTreeUtil.findChildOfType(binding, PsiRecord.class));
    }

    public void testRecord() {
        PsiLet let = first(letExpressions(parseCode("let typeScale = {one: 1.375, two: 1.0};")));

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
        PsiRecord record = PsiTreeUtil.findChildOfType(binding, PsiRecord.class);
        assertNotNull(record);
        assertSize(2, record.getFields());
    }

    public void testSignature() {
        PsiLet let = first(letExpressions(parseCode("let combine: (style, style) => style = (a, b) => { };")));

        assertEquals("(style, style) -> style", let.getSignature().toString());
        assertEquals("(a, b) => { }", let.getBinding().getText());
    }

    public void testRec() {
        PsiLet let = first(letExpressions(parseCode("let rec lx = x => x + 1")));

        assertTrue(let.isFunction());
        assertEquals("lx", let.getName());
    }

    public void testWithType() {
        PsiFile file = parseCode("let cast_list: type a b. (eq(a, b), list(a)) => list(b) = (Refl, x) => x;");
        PsiLet let = first(letExpressions(file));

        assertEquals("type a b. (eq(a, b), list(a)) -> list(b)", let.getSignature().toString());
        assertEquals("(Refl, x) => x", let.getFunction().getText());
    }

}
