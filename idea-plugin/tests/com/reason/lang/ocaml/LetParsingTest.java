package com.reason.lang.ocaml;

import java.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.ExpressionScope;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.PsiRecordField;

import static com.reason.lang.core.ExpressionFilterConstants.NO_FILTER;

@SuppressWarnings("ConstantConditions")
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
        PsiLet e = first(letExpressions(parseCode("let fn p = Js.log p; returnObj")));

        assertTrue(e.isFunction());
        assertEquals("fn", e.getName());
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
        FileBase file = parseCode("let module Repr = (val repr : S)");
        PsiModule module = first(moduleExpressions(file));

        assertEquals(1, childrenCount(file));
        assertEquals("Repr", module.getName());
    }

    public void testChaining() {
        PsiFile file = parseCode("let visit_vo f = let segments = [| a; b; |] in let repr = x");
        Collection<PsiLet> lets = letExpressions(file);

        assertEquals(1, lets.size());
    }

    public void testCase1() {
        FileBase file = parseCode("let format_open {o_loc; o_name; o_items; _} = "
                                          + "Printf.printf \"O|%s|%s|%s\\n\" (format_location o_loc) o_name (Util.join_list \", \" !o_items)");
        PsiLet e = first(letExpressions(file));

        PsiLetBinding binding = e.getBinding();
        assertInstanceOf(binding.getFirstChild(), PsiFunction.class);
        PsiFunction function = (PsiFunction) binding.getFirstChild();
        assertEquals("{o_loc; o_name; o_items; _}", first(function.getParameters()).getText());
        assertEquals("Printf.printf \"O|%s|%s|%s\\n\" (format_location o_loc) o_name (Util.join_list \", \" !o_items)", function.getBody().getText());
    }

    public void testGH_116() {
        FileBase file = parseCode("let ((), proofview, _, _) = Proofview.apply (Global.env ()) tac pr.proofview");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("Proofview.apply (Global.env ()) tac pr.proofview", e.getBinding().getText());
    }

    public void testGH_105() {
        FileBase file = parseCode("let string = \"x\"");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("string", e.getName());
    }

    public void testIssue105a() {
        FileBase file = parseCode("let string s = \"x\"");
        PsiLet e = first(letExpressions(file));

        assertTrue(e.isFunction());
        assertEquals("string", e.getName());
    }

    public void testIssue105b() {
        FileBase file = parseCode("let int = 1");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("int", e.getName());
    }

    public void testIssue105c() {
        FileBase file = parseCode("let bool = 1");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("bool", e.getName());
    }

    public void testIssue121() {
        Collection<PsiLet> lets = letExpressions(parseCode("let rec f x y = match x with | [] -> return y\n" + "let x =  1"));

        assertSize(2, lets);
    }

    public void testQualifiedName() {
        PsiLet root = first(letExpressions(parseCode("let root = x")));
        PsiLet inner = PsiTreeUtil.findChildOfType(first(letExpressions(parseCode("let root = let inner = x in inner"))), PsiLet.class);
        PsiModule mod = first(moduleExpressions(parseCode("module M = struct let m = 1 end")));

        assertEquals("Dummy.root", root.getQualifiedName());
        assertEquals("Dummy.root.inner", inner.getQualifiedName());
        assertEquals("Dummy.M.m", ((PsiLet) mod.getExpressions(ExpressionScope.all, NO_FILTER).iterator().next()).getQualifiedName());
    }

    public void testDeconstruction() {
        PsiLet e = first(letExpressions(parseCode("let (a, b) = x;")));

        assertTrue(e.isDeconsruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertEquals("b", names.get(1).getText());
    }
}
