package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class LetParsingTest extends OclParsingTestCase {
    @Test
    public void test_constant() {
        FileBase file = parseCode("let x = 1 let y = 2");
        List<RPsiLet> lets = ORUtil.findImmediateChildrenOfClass(file, RPsiLet.class);

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
        RPsiLet let = firstOfType(parseCode("let obj = [%bs.obj { a = \"b\" }];"), RPsiLet.class);

        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_binding_with_function() {
        RPsiLet e = firstOfType(parseCode("let add x y = x + y"), RPsiLet.class);

        assertNotNull(first(PsiTreeUtil.findChildrenOfType(e, RPsiLetBinding.class)));
        assertEquals("x y = x + y", e.getBinding().getText());
    }

    @Test
    public void test_scope_with_some() {
        RPsiLet let = firstOfType(parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };"), RPsiLet.class);

        RPsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class));
        assertNotNull(binding);
    }

    @Test
    public void test_scope_with_lIdent() {
        RPsiLet e = firstOfType(parseCode("let fn p = Js.log p; returnObj"), RPsiLet.class);

        assertTrue(e.isFunction());
        assertEquals("fn", e.getName());
    }

    @Test
    public void test_record() {
        RPsiLet let = firstOfType(parseCode("let r = { one = 1; two = 2 }"), RPsiLet.class);

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
        RPsiLet let = firstOfType(parseCode("let rec lx x = x + 1"), RPsiLet.class);

        assertTrue(let.isFunction());
        assertEquals("lx", let.getName());
    }

    @Test
    public void test_in_do_loop() {
        FileBase file = parseCode("let x l = for i = 0 to l - 1 do let x = 1 done");
        RPsiLet let = firstOfType(file, RPsiLet.class);

        assertTrue(let.isFunction());
        assertEquals("l = for i = 0 to l - 1 do let x = 1 done", let.getBinding().getText());
    }

    @Test
    public void test_with_semi_separator() {
        FileBase file = parseCode("let rec read_num = Printf.printf; let l = 1");
        Collection<RPsiLet> lets = ORUtil.findImmediateChildrenOfClass(file, RPsiLet.class);

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
        RPsiInnerModule module = first(moduleExpressions(file));

        assertEquals(1, childrenCount(file));
        assertEquals("Repr", module.getName());
    }

    @Test
    public void test_chaining() {
        FileBase file = parseCode("let visit_vo f = let segments = [| a; b; |] in let repr = x");
        List<RPsiLet> lets = ORUtil.findImmediateChildrenOfClass(file, RPsiLet.class);

        assertEquals(1, lets.size());
    }

    @Test
    public void test_case1() {
        FileBase file = parseCode("let format_open {o_loc; o_name; o_items; _} = "
                + "Printf.printf \"O|%s|%s|%s\\n\" (format_location o_loc) o_name (join_list \", \" !o_items)");
        RPsiLet e = firstOfType(file, RPsiLet.class);

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
        RPsiLet root = firstOfType(parseCode("let root = x"), RPsiLet.class);
        RPsiLet inner = PsiTreeUtil.findChildOfType(firstOfType(parseCode("let root = let inner = x in inner"), RPsiLet.class), RPsiLet.class);
        RPsiInnerModule mod = first(moduleExpressions(parseCode("module M = struct let m = 1 end")));

        assertEquals("Dummy.root", root.getQualifiedName());
        assertEquals("Dummy.root.inner", inner.getQualifiedName());
        assertEquals("Dummy.M.m", ORUtil.findImmediateFirstChildOfClass(mod.getBody(), RPsiLet.class).getQualifiedName());
    }

    @Test
    public void test_deconstruction() {
        RPsiLet e = firstOfType(parseCode("let (a, b) = x"), RPsiLet.class);

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
        RPsiLet e = firstOfType(parseCode("let a, b = x"), RPsiLet.class);

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
        RPsiLet e = firstOfType(parseCode("let { a; b } = x"), RPsiLet.class);

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
        RPsiLet e = firstOfType(parseCode("let tokens = [ \"bullet\"; \"string\"; \"unicode_id_part\"; ]"), RPsiLet.class);

        assertEquals("[ \"bullet\"; \"string\"; \"unicode_id_part\"; ]", e.getBinding().getText());
    }

    @Test
    public void test_optional_param() {
        RPsiLet e = firstOfType(parseCode("let prod_to_str ?(plist=false) prod = String.concat \" \" (prod_to_str_r plist prod)"), RPsiLet.class);

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
        RPsiLet e = firstOfType(file, RPsiLet.class);

        assertFalse(e.isFunction());
        assertEquals("string", e.getName());
    }

    @Test
    public void test_GH_105a() {
        FileBase file = parseCode("let string s = \"x\"");
        RPsiLet e = firstOfType(file, RPsiLet.class);

        assertTrue(e.isFunction());
        assertEquals("string", e.getName());
    }

    @Test
    public void test_GH_105b() {
        FileBase file = parseCode("let int = 1");
        RPsiLet e = firstOfType(file, RPsiLet.class);

        assertFalse(e.isFunction());
        assertEquals("int", e.getName());
    }

    @Test
    public void test_GH_105c() {
        FileBase file = parseCode("let bool = 1");
        RPsiLet e = firstOfType(file, RPsiLet.class);

        assertFalse(e.isFunction());
        assertEquals("bool", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/116
    @Test
    public void test_GH_116() {
        FileBase file = parseCode("let ((), proofview, _, _) = Proofview.apply (Global.env ()) tac pr.proofview");
        RPsiLet e = firstOfType(file, RPsiLet.class);

        assertSize(2, e.getDeconstructedElements());
        assertFalse(e.isFunction());
        assertEquals("Proofview.apply (Global.env ()) tac pr.proofview", e.getBinding().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/121
    @Test
    public void test_GH_121() {
        Collection<RPsiLet> lets = ORUtil.findImmediateChildrenOfClass(parseCode("let rec f x y = match x with | [] -> return y\n let x =  1"), RPsiLet.class);

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
        RPsiLet e = firstOfType(parseCode("let (//) = Ext_path.combine"), RPsiLet.class);

        assertEquals("(//)", e.getName());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiComment.class));
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/406
    @Test
    public void test_GH_406() {
        RPsiLet e = firstOfType(parseCode("let is_uppercase s =\n" +
                "  let x = 0 in\n" +
                "  let open CamomileLibraryDefault.Camomile in\n" +
                "  let open UReStr in\n" +
                "  let open UReStr.Make(UTF8) in\n" +
                "  let y = 0 in\n" +
                "  let z = 0 in\n" +
                "  false"), RPsiLet.class);

        assertNoParserError(e);
        RPsiFunctionBody eb = e.getFunction().getBody();
        RPsiLet[] ebls = PsiTreeUtil.getChildrenOfType(eb, RPsiLet.class);
        assertEquals("let x = 0", ebls[0].getText());
        assertEquals("let y = 0", ebls[1].getText());
        assertEquals("let z = 0", ebls[2].getText());
        assertSize(3, ebls);
        RPsiOpen[] ebos = PsiTreeUtil.getChildrenOfType(eb, RPsiOpen.class);
        assertEquals("let open CamomileLibraryDefault.Camomile", ebos[0].getText());
        assertEquals("let open UReStr", ebos[1].getText());
        assertEquals("let open UReStr.Make(UTF8)", ebos[2].getText());
        assertSize(3, ebos);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/409
    @Test
    public void test_GH_409() {
        RPsiLet e = firstOfType(parseCode("let f () =\n" +
                "  let b : bool = 1 = 1 in\n" +
                "  let x = 0 in\n" +
                "  x"), RPsiLet.class);

        assertNoParserError(e);
        RPsiFunctionBody eb = e.getFunction().getBody();
        RPsiLet[] ebls = PsiTreeUtil.getChildrenOfType(eb, RPsiLet.class);
        assertEquals("let b : bool = 1 = 1", ebls[0].getText());
        assertEquals("let x = 0", ebls[1].getText());
        assertSize(2, ebls);
    }
}
