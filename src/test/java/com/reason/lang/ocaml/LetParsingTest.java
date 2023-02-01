package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

import static com.reason.lang.core.ExpressionFilterConstants.*;

@SuppressWarnings("ConstantConditions")
public class LetParsingTest extends OclParsingTestCase {
    @Test
    public void test_constant() {
        FileBase file = parseCode("let x = 1 let y = 2");
        List<RPsiLet> lets = new ArrayList<>(letExpressions(file));

        assertEquals(2, lets.size());
        assertEquals("x", lets.get(0).getName());
        assertFalse(lets.get(0).isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(lets.get(0), RPsiLetBinding.class)));
    }

    @Test
    public void test_underscore() {
        RPsiLet e = firstOfType(parseCode("let _ = ()"), RPsiLet.class);
        assertNull(e.getName());
        assertNotNull(e.getNavigationElement());
    }

    @Test
    public void test_binding() {
        RPsiLet let = first(letExpressions(parseCode("let obj = [%bs.obj { a = \"b\" }];")));

        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_binding_with_function() {
        RPsiLet e = first(letExpressions(parseCode("let add x y = x + y")));

        assertNotNull(first(PsiTreeUtil.findChildrenOfType(e, RPsiLetBinding.class)));
        assertEquals("x y = x + y", e.getBinding().getText());
    }

    @Test
    public void test_scope_with_some() {
        RPsiLet let = first(letExpressions(
                parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };")));

        RPsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class));
        assertNotNull(binding);
    }

    @Test
    public void test_scope_with_lIdent() {
        RPsiLet e = first(letExpressions(parseCode("let fn p = Js.log p; returnObj")));

        assertTrue(e.isFunction());
        assertEquals("fn", e.getName());
    }

    @Test
    public void test_record() {
        RPsiLet let = first(letExpressions(parseCode("let r = { one = 1; two = 2 }")));

        RPsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class));
        assertNotNull(binding);
        RPsiRecord record = PsiTreeUtil.findChildOfType(binding, RPsiRecord.class);
        assertNotNull(record);
        Collection<RPsiRecordField> fields = record.getFields();
        assertSize(2, fields);
        Iterator<RPsiRecordField> itFields = fields.iterator();
        assertEquals("one = 1", itFields.next().getText());
        assertEquals("two = 2", itFields.next().getText());
    }

    @Test
    public void test_rec() {
        RPsiLet let = first(letExpressions(parseCode("let rec lx x = x + 1")));

        assertTrue(let.isFunction());
        assertEquals("lx", let.getName());
    }

    @Test
    public void test_in_do_loop() {
        FileBase file = parseCode("let x l = for i = 0 to l - 1 do let x = 1 done");
        RPsiLet let = first(letExpressions(file));

        assertTrue(let.isFunction());
        assertEquals("l = for i = 0 to l - 1 do let x = 1 done", let.getBinding().getText());
    }

    @Test
    public void test_with_semi_separator() {
        FileBase file = parseCode("let rec read_num = Printf.printf; let l = 1");
        Collection<RPsiLet> lets = letExpressions(file);

        assertEquals(1, lets.size());
    }

    @Test
    public void test_like_local_open() {
        RPsiOpen open = first(openExpressions(parseCode("let open Univ")));

        assertEquals("let open Univ", open.getText());
    }

    @Test
    public void test_like_module() {
        FileBase file = parseCode("let module Repr = (val repr : S)");
        RPsiModule module = first(moduleExpressions(file));

        assertEquals(1, childrenCount(file));
        assertEquals("Repr", module.getName());
    }

    @Test
    public void test_chaining() {
        FileBase file = parseCode("let visit_vo f = let segments = [| a; b; |] in let repr = x");
        Collection<RPsiLet> lets = letExpressions(file);

        assertEquals(1, lets.size());
    }

    @Test
    public void test_case1() {
        FileBase file = parseCode("let format_open {o_loc; o_name; o_items; _} = "
                + "Printf.printf \"O|%s|%s|%s\\n\" (format_location o_loc) o_name (join_list \", \" !o_items)");
        RPsiLet e = first(letExpressions(file));

        RPsiLetBinding binding = e.getBinding();
        assertInstanceOf(binding.getFirstChild(), RPsiFunction.class);
        RPsiFunction function = (RPsiFunction) binding.getFirstChild();
        assertEquals("{o_loc; o_name; o_items; _}", first(function.getParameters()).getText());
        assertEquals("Printf.printf \"O|%s|%s|%s\\n\" (format_location o_loc) o_name (join_list \", \" !o_items)", function.getBody().getText());
        RPsiFunctionCall fc = ORUtil.findImmediateFirstChildOfClass(function.getBody(), RPsiFunctionCall.class);
        assertEquals("(format_location o_loc)", fc.getParameters().get(1).getText());
    }

    @Test
    public void test_qualifiedName() {
        RPsiLet root = first(letExpressions(parseCode("let root = x")));
        RPsiLet inner = PsiTreeUtil.findChildOfType(first(letExpressions(parseCode("let root = let inner = x in inner"))), RPsiLet.class);
        RPsiModule mod = first(moduleExpressions(parseCode("module M = struct let m = 1 end")));

        assertEquals("Dummy.root", root.getQualifiedName());
        assertEquals("Dummy.root.inner", inner.getQualifiedName());
        assertEquals("Dummy.M.m", ((RPsiLet) mod.getExpressions(ExpressionScope.all, NO_FILTER).iterator().next()).getQualifiedName());
    }

    @Test
    public void test_deconstruction() {
        RPsiLet e = first(letExpressions(parseCode("let (a, b) = x")));

        assertTrue(e.isDeconstruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertInstanceOf(names.get(0), RPsiLowerSymbol.class);
        assertEquals("b", names.get(1).getText());
        assertInstanceOf(names.get(1), RPsiLowerSymbol.class);
    }

    @Test
    public void test_deconstruction_parenless() {
        RPsiLet e = first(letExpressions(parseCode("let a, b = x")));

        assertTrue(e.isDeconstruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertInstanceOf(names.get(0), RPsiLowerSymbol.class);
        assertEquals("b", names.get(1).getText());
        assertInstanceOf(names.get(1), RPsiLowerSymbol.class);
    }

    @Test
    public void test_deconstruction_nested() { // belt_Map offset 2272
        RPsiLet e = firstOfType(parseCode("let (l,r),b = Dict.split ~cmp m.data x"), RPsiLet.class);

        assertTrue(e.isDeconstruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(3, names);
        assertEquals("l", names.get(0).getText());
        assertInstanceOf(names.get(0), RPsiLowerSymbol.class);
        assertEquals("r", names.get(1).getText());
        assertInstanceOf(names.get(1), RPsiLowerSymbol.class);
        assertEquals("b", names.get(2).getText());
        assertInstanceOf(names.get(2), RPsiLowerSymbol.class);
    }

    @Test
    public void test_deconstruction_braces() {
        RPsiLet e = first(letExpressions(parseCode("let { a; b } = x")));

        assertTrue(e.isDeconstruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertInstanceOf(names.get(0), RPsiLowerSymbol.class);
        assertEquals("b", names.get(1).getText());
        assertInstanceOf(names.get(1), RPsiLowerSymbol.class);
    }

    @Test
    public void test_List() {
        RPsiLet e = first(letExpressions(parseCode("let tokens = [ \"bullet\"; \"string\"; \"unicode_id_part\"; ]")));

        assertEquals("[ \"bullet\"; \"string\"; \"unicode_id_part\"; ]", e.getBinding().getText());
    }

    @Test
    public void test_optional_param() {
        RPsiLet e = first(letExpressions(parseCode("let prod_to_str ?(plist=false) prod = String.concat \" \" (prod_to_str_r plist prod)")));

        assertTrue(e.isFunction());
        assertEquals("?(plist=false) prod = String.concat \" \" (prod_to_str_r plist prod)", e.getBinding().getText());
    }

    @Test
    public void test_comparison() {
        RPsiLet e = firstOfType(parseCode("let lt x y = (x lxor 0x4) < (y lxor 0x4)\ntype t"), RPsiLet.class);
        RPsiFunctionBody b = e.getFunction().getBody();

        assertEquals("(x lxor 0x4) < (y lxor 0x4)", b.getText());
    }

    @Test
    public void test_semi() {
        RPsiLet e = firstOfType(parseCode("let _ = for i = 0 to len - 1 do done; get_data p"), RPsiLet.class);

        assertEquals("for i = 0 to len - 1 do done; get_data p", e.getBinding().getText());
    }

    @Test
    public void test_name_unit() {
        RPsiLet e = firstOfType(parseCode("let () = 1 + 2"), RPsiLet.class);

        assertNotNull(PsiTreeUtil.findChildOfType(e, RPsiUnit.class));
        assertNull(e.getName());
    }

    @Test
    public void test_parens() {
        RPsiLetBinding e = firstOfType(parseCode("let tmp = uget t ((pred n)-i)"), RPsiLetBinding.class);

        assertNoParserError(e);
        assertEquals("uget t ((pred n)-i)", e.getText());
    }

    // should it be parsed like a function ?
    // https://github.com/giraud/reasonml-idea-plugin/issues/309
    @Test
    public void test_infix_operator() {
        RPsiLet e = firstOfType(parseCode("let (|?) m (key, cb) = m |> Ext_json.test key cb"), RPsiLet.class);

        assertEquals("(|?)", e.getName());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiPatternMatch.class));
        assertEquals("m |> Ext_json.test key cb", e.getBinding().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105() {
        FileBase file = parseCode("let string = \"x\"");
        RPsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("string", e.getName());
    }

    @Test
    public void test_GH_105a() {
        FileBase file = parseCode("let string s = \"x\"");
        RPsiLet e = first(letExpressions(file));

        assertTrue(e.isFunction());
        assertEquals("string", e.getName());
    }

    @Test
    public void test_GH_105b() {
        FileBase file = parseCode("let int = 1");
        RPsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("int", e.getName());
    }

    @Test
    public void test_GH_105c() {
        FileBase file = parseCode("let bool = 1");
        RPsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("bool", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/116
    @Test
    public void test_GH_116() {
        FileBase file = parseCode("let ((), proofview, _, _) = Proofview.apply (Global.env ()) tac pr.proofview");
        RPsiLet e = first(letExpressions(file));

        assertSize(2, e.getDeconstructedElements());
        assertFalse(e.isFunction());
        assertEquals("Proofview.apply (Global.env ()) tac pr.proofview", e.getBinding().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/121
    @Test
    public void test_GH_121() {
        Collection<RPsiLet> lets = letExpressions(parseCode("let rec f x y = match x with | [] -> return y\n let x =  1"));

        assertSize(2, lets);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/270
    // https://caml.inria.fr/pub/docs/manual-ocaml/polymorphism.html#ss:explicit-polymorphism
    @Test
    public void test_GH_270() {
        RPsiLet e = firstOfType(parseCode("let rec parser_of_tree : type s tr r. s ty_entry -> int -> int -> (s, tr, r) ty_tree -> r parser_t =\n"
                + "  fun entry nlevn alevn -> ()"), RPsiLet.class);

        assertEquals("parser_of_tree", e.getName());
        assertTrue(e.isFunction());
        assertEquals("s ty_entry -> int -> int -> (s, tr, r) ty_tree -> r parser_t", e.getSignature().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/278
    @Test
    public void test_GH_278() {
        RPsiLet e = first(letExpressions(parseCode("let (//) = Ext_path.combine")));

        assertEquals("(//)", e.getName());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiComment.class));
    }
}
