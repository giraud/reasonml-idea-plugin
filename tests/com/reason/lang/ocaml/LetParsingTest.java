package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class LetParsingTest extends BaseParsingTestCase {
    public LetParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testConstant() {
        PsiFile file = parseCode("let x = 1 let y = 2");
        List<PsiLet> lets = new ArrayList<>(letExpressions(file));

        assertEquals(2, lets.size());
        assertEquals("x", lets.get(0).getName());
        assertFalse(lets.get(0).isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(lets.get(0), PsiLetBinding.class)));
    }

    public void testLetBinding() {
        PsiLet let = first(letExpressions(parseCode("let obj = [%bs.obj { a = \"b\" }];")));

        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testLetBindingWithFunction() {
        PsiLet let = first(letExpressions(parseCode("let add x y = x + y")));
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
        assertEquals("x y = x + y", let.getBinding().getText());
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
        PsiFile file = parseCode("let x l = for i = 0 to l - 1 do let x = 1 done");
        PsiLet let = first(letExpressions(file));

        assertTrue(let.isFunction());
        assertEquals("l = for i = 0 to l - 1 do let x = 1 done", let.getBinding().getText());
    }

    public void testWithSemiSeparator() {
        PsiFile file = parseCode("let rec read_num = Printf.printf; let l = 1");
        Collection<PsiLet> lets = letExpressions(file);

        assertEquals(1, lets.size());
    }

    public void testLikeLocalOpen() {
        PsiOpen open = first(openExpressions(parseCode("let open Univ")));

        assertEquals("let open Univ", open.getText());
    }

    public void testLikeModule() {
        PsiFile file = parseCode("let module Repr = (val repr : S)");
        PsiModule module = first(moduleExpressions(file));

        assertEquals(1, file.getChildren().length);
        assertEquals("Repr", module.getName());
    }

    public void testChaining() {
        PsiFile file = parseCode("let visit_vo f = let segments = [| a; b; |] in let repr = x");
        Collection<PsiLet> lets = letExpressions(file);

        assertEquals(1, lets.size());
    }

    public void testCase1() {
        FileBase file = parseCode("let format_open {o_loc; o_name; o_items; _} = " +
                "Printf.printf \"O|%s|%s|%s\\n\" (format_location o_loc) o_name (Util.join_list \", \" !o_items)");
        PsiLet e = first(letExpressions(file));

        PsiLetBinding binding = e.getBinding();
        assertInstanceOf(binding.getFirstChild(), PsiFunction.class);
        PsiFunction function = (PsiFunction) binding.getFirstChild();
        assertEquals("{o_loc; o_name; o_items; _}", function.getParameterList().iterator().next().getText());
        assertEquals("Printf.printf \"O|%s|%s|%s\\n\" (format_location o_loc) o_name (Util.join_list \", \" !o_items)", function.getBody().getText());
    }
}
