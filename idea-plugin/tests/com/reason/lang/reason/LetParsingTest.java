package com.reason.lang.reason;

import java.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.PsiFileHelper;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.signature.ORSignature;

import static com.reason.lang.core.ExpressionFilterConstants.FILTER_LET;
import static com.reason.lang.core.psi.ExpressionScope.pub;

@SuppressWarnings("ConstantConditions")
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
        PsiElement element = PsiTreeUtil.nextLeaf(children[1]);

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

        assertEquals("(style, style) => style", let.getORSignature().asString(RmlLanguage.INSTANCE));
        assertEquals("(a, b) => { }", let.getBinding().getText());
    }

    public void testRec() {
        PsiLet let = first(letExpressions(parseCode("let rec lx = x => x + 1")));

        assertTrue(let.isFunction());
        assertEquals("lx", let.getName());
    }

    public void testIssue105() {
        FileBase file = parseCode("let string = \"x\"");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("string", e.getName());
    }

    public void testIssue105a() {
        FileBase file = parseCode("let int = 1");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("int", e.getName());
    }

    public void testIssue105b() {
        FileBase file = parseCode("let bool = 1");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("bool", e.getName());
    }

    public void testSignatureB() {
        FileBase file = parseCode("let watchUrl: (url => unit) => watcherID;");
        PsiLet e = first(letExpressions(file));

        assertTrue(e.isFunction());
        assertEquals("watchUrl", e.getName());
        assertEquals("(url => unit) => watcherID", e.getPsiSignature().getText());
    }

    public void testLetAndInModule() {
        FileBase file = parseCode("module M = { let f1 = x => x and f2 = y => y; };");
        Collection<PsiNameIdentifierOwner> es = PsiFileHelper.getModuleExpressions(file).iterator().next().getExpressions(pub, FILTER_LET);

        assertSize(2, es);
        assertEquals("f2 = y => y", second(es).getText());
    }

    public void testAlias() {
        PsiLet e = first(letExpressions(parseCode("let x = M1.M2.y;")));

        assertEquals("x", e.getName());
        assertEquals("M1.M2.y", e.getAlias());
    }

    public void testDeconstruction() {
        PsiLet e = first(letExpressions(parseCode("let (a, b) = x;")));

        assertTrue(e.isDeconsruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertEquals("b", names.get(1).getText());
    }

    public void testOperator() {
        PsiLet e = first(letExpressions(parseCode("let (/): (path('a, 'b) => 'c, 'd => path('a, 'b), 'd) => 'c;")));

        assertEquals("(/)", e.getName());
        ORSignature signature = e.getORSignature();
        assertEquals("(path('a, 'b) => 'c, 'd => path('a, 'b), 'd) => 'c", signature.asString(RmlLanguage.INSTANCE));
    }

    public void testPrivate() {
        PsiLet e = first(letExpressions(parseCode("let x%private = 1;")));

        assertEquals("x", e.getName());
        assertTrue(e.isPrivate());
    }
}
