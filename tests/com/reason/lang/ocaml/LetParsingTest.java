package com.reason.lang.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

import java.util.Collection;
import java.util.Iterator;

public class LetParsingTest extends BaseParsingTestCase {
    public LetParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testConstant() {
        PsiLet let = first(letExpressions(parseCode("let x = 1")));

        assertEquals("x", let.getName());
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }


    public void testLetBinding() {
        PsiLet let = first(letExpressions(parseCode("let obj = [%bs.obj { a = \"b\" }];")));

        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testLetBindingWithFunction() {
        PsiLet let = first(letExpressions(parseCode("let add x y = x + y")));
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testScopeWithSome() {
        PsiLet let = first(letExpressions(parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };")));

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
    }

    public void testScopeWithLIdent() {
        PsiLet let = first(letExpressions(parseCode("let l p = Js.log p; returnObj")));

        assertTrue(let.isFunction());
    }

    public void testRecord() {
        PsiLet let = first(letExpressions(parseCode("let r = { one = 1; two = 2 }")));

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
        PsiRecord record = PsiTreeUtil.findChildOfType(binding, PsiRecord.class);
        assertNotNull(record);
        Collection<PsiRecordField> fields = record.getFields();
        assertSize(2, fields);
        Iterator<PsiRecordField> itFields = fields.iterator();
        assertEquals("one = 1", itFields.next().getText());
        assertEquals("two = 2", itFields.next().getText());
    }

    public void testRec() {
        PsiLet let = first(letExpressions(parseCode("let rec lx x = x + 1")));

        assertTrue(let.isFunction());
        assertEquals("lx", let.getName());
    }

    public void testInDoLoop() {
        PsiLet let = first(letExpressions(parseCode("let x l = for i = 0 to l - 1 do let x = 1 done")));

        assertTrue(let.isFunction());
        assertEquals("l = for i = 0 to l - 1 do let x = 1 done", let.getBinding().getText());
    }

    public void testLikeLocalOpen() {
        PsiOpen open = first(openExpressions(parseCode("let open Univ")));

        assertEquals("let open Univ", open.getText());
    }

}
