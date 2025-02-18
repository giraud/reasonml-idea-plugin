package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class LetParsingTest extends RmlParsingTestCase {
    @Test
    public void test_constant() {
        RPsiLet let = firstOfType(parseCode("let x = 1;"), RPsiLet.class);

        assertEquals("x", let.getName());
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_function_let_binding() {
        RPsiLet let = firstOfType(parseCode("let getAttributes = node => { node; };"), RPsiLet.class);

        assertTrue(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_unit_function() {
        RPsiLet e = firstOfType(parseCode("let x = () => 1;"), RPsiLet.class);

        assertTrue(e.isFunction());
        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        assertEquals("1", function.getBody().getText());
    }

    @Test
    public void test_binding() {
        RPsiLet let = firstOfType(parseCode("let x = {\"u\": \"r\", \"l\": \"lr\"};"), RPsiLet.class);
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_binding_with_jsx() {
        PsiFile file = parseCode("let make = p => { render: x => { <div/>; } }");
        assertSize(1, expressions(file));
    }

    @Test
    public void test_scope_with_some() {
        RPsiLet let = firstOfType(parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };"), RPsiLet.class);

        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_scope_with_LIdent() {
        RPsiLet let = firstOfType(parseCode("let l = (p) => { Js.log(p); returnObj; };"), RPsiLet.class);

        RPsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class));
        assertNotNull(binding);
    }

    @Test
    public void test_local_scope() {
        RPsiLet let = firstOfType(parseCode("let x = { let y = 1; y + 3; }"), RPsiLet.class);

        RPsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class));
        assertNotNull(binding);
        assertNull(PsiTreeUtil.findChildOfType(binding, RPsiRecord.class));
    }

    @Test
    public void test_record() {
        RPsiLet let = firstOfType(parseCode("let typeScale = {one: 1.375, two: 1.0};"), RPsiLet.class);

        RPsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class));
        RPsiRecord record = PsiTreeUtil.findChildOfType(binding, RPsiRecord.class);
        assertSize(2, record.getFields());
    }

    @Test
    public void test_signature() {
        RPsiLet let = firstOfType(parseCode("let combine: (style, style) => style = (a, b) => { };"), RPsiLet.class);

        assertEquals("(style, style) => style", let.getSignature().getText());
        assertEquals("(a, b) => { }", let.getBinding().getText());
    }

    @Test
    public void test_signature_dot() {
        RPsiLet let = firstOfType(parseCode("let x: M1.y => M2.z;"), RPsiLet.class);

        assertNull(PsiTreeUtil.findChildOfType(let, RPsiFunction.class));
        assertEquals("M1.y => M2.z", let.getSignature().getText());
        List<RPsiSignatureItem> items = new ArrayList<>(PsiTreeUtil.findChildrenOfType(let.getSignature(), RPsiSignatureItem.class));
        assertEquals("M1.y", items.get(0).getText());
    }

    @Test
    public void test_signature_JsObject() {
        RPsiLet let = firstOfType(parseCode("let x: {. a:string, b:int } => unit;"), RPsiLet.class);

        assertEquals("{. a:string, b:int } => unit", let.getSignature().getText());
        List<RPsiObjectField> fields = new ArrayList<>(PsiTreeUtil.findChildrenOfType(let, RPsiObjectField.class));
        assertEquals("a:string", fields.get(0).getText());
        assertEquals("b:int", fields.get(1).getText());
    }

    @Test
    public void test_rec() {
        RPsiLet let = firstOfType(parseCode("let rec lx = x => x + 1"), RPsiLet.class);

        assertTrue(let.isFunction());
        assertEquals("lx", let.getName());
    }

    @Test
    public void test_signatureB() {
        FileBase file = parseCode("let watchUrl: (url => unit) => watcherID;");
        RPsiLet e = firstOfType(file, RPsiLet.class);

        assertTrue(e.isFunction());
        assertEquals("watchUrl", e.getName());
        assertEquals("(url => unit) => watcherID", e.getSignature().getText());
    }

    @Test
    public void test_let_and_in_module() {
        FileBase file = parseCode("module M = { let f1 = x => x and f2 = y => y; };");
        Collection<PsiNamedElement> es = PsiTreeUtil.findChildrenOfType(file, RPsiLet.class);

        assertSize(2, es);
        assertEquals("f2 = y => y", second(es).getText());
    }

    @Test
    public void test_alias() {
        RPsiLet e = firstOfType(parseCode("let x = M1.M2.y;"), RPsiLet.class);

        assertEquals("x", e.getName());
        assertEquals("M1.M2.y", e.getAlias());
    }

    @Test
    public void test_variant() {
        RPsiLet e = firstOfType(parseCode("let x = MyVar;"), RPsiLet.class);

        assertEquals("x", e.getName());
        assertEquals("MyVar", e.getAlias());
        assertEquals(RmlTypes.INSTANCE.A_VARIANT_NAME, e.getBinding().getFirstChild().getNode().getElementType());
    }

    @Test
    public void test_deconstruction() {
        RPsiLet e = firstOfType(parseCode("let (a, b) = x;"), RPsiLet.class);

        assertTrue(e.isDeconstruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertInstanceOf(names.get(0), RPsiLowerName.class);
        assertEquals("b", names.get(1).getText());
        assertInstanceOf(names.get(1), RPsiLowerName.class);
    }

    @Test
    public void test_deconstruction_nested() { // belt_Map offset 2272
        RPsiLet e = firstOfType(parseCode("let ((l, r), b) = Dict.split(~cmp, m.data, x);"), RPsiLet.class);

        assertTrue(e.isDeconstruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(3, names);
        assertEquals("l", names.get(0).getText());
        assertInstanceOf(names.get(0), RPsiLowerName.class);
        assertEquals("r", names.get(1).getText());
        assertInstanceOf(names.get(1), RPsiLowerName.class);
        assertEquals("b", names.get(2).getText());
        assertInstanceOf(names.get(2), RPsiLowerName.class);
    }

    @Test
    public void test_deconstruction_braces() {
        RPsiLet e = firstOfType(parseCode("let {a, b, _} = x;"), RPsiLet.class);

        assertEquals("x", e.getBinding().getText());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertInstanceOf(names.get(0), RPsiLowerName.class);
        assertEquals("b", names.get(1).getText());
        assertInstanceOf(names.get(1), RPsiLowerName.class);
    }

    @Test
    public void test_operator() {
        RPsiLet e = firstOfType(parseCode("let (/): (path('a, 'b) => 'c, 'd => path('a, 'b), 'd) => 'c;"), RPsiLet.class);

        assertEquals("(/)", e.getName());
        // ORSignature signature = e.getSignature();
        // assertEquals("(path('a, 'b) => 'c, 'd => path('a, 'b), 'd) => 'c",
        // signature.asString(myLanguage));
    }

    @Test
    public void test_private() {
        RPsiLet e = firstOfType(parseCode("let x%private = 1;"), RPsiLet.class);

        assertEquals("x", e.getName());
        assertTrue(e.isPrivate());
    }

    @Test
    public void test_function_record() {
        List<RPsiLet> es = ORUtil.findImmediateChildrenOfClass(parseCode("let x = y(M.{i: 1}); let z=2;"), RPsiLet.class);

        assertSize(2, es);
        assertNull(PsiTreeUtil.findChildOfType(es.get(0), RPsiScopedExpr.class));
        assertEquals("i", PsiTreeUtil.findChildOfType(es.get(0), RPsiRecordField.class).getName());
        assertEquals("{i: 1}", PsiTreeUtil.findChildOfType(es.get(0), RPsiRecord.class).getText());
    }

    @Test
    public void test_braces() {
        RPsiLet e = firstOfType(parseCode("let x = p => { test ? { call(Some(a)); } : b };"), RPsiLet.class);

        assertEquals("x", e.getName());
        assertTrue(e.isFunction());
        RPsiFunction f = e.getFunction();
        assertEquals("{ test ? { call(Some(a)); } : b }", f.getBody().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105() {
        FileBase file = parseCode("let string = \"x\"");
        RPsiLet e = firstOfType(file, RPsiLet.class);

        assertFalse(e.isFunction());
        assertEquals("string", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105a() {
        FileBase file = parseCode("let int = 1");
        RPsiLet e = firstOfType(file, RPsiLet.class);

        assertFalse(e.isFunction());
        assertEquals("int", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105b() {
        FileBase file = parseCode("let bool = 1");
        RPsiLet e = firstOfType(file, RPsiLet.class);

        assertFalse(e.isFunction());
        assertEquals("bool", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/278
    @Test
    public void test_GH_278() {
        RPsiLet e = firstOfType(parseCode("let (/\\/) = Ext_path.combine"), RPsiLet.class);

        assertEquals("(/\\/)", e.getName());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiComment.class));
    }
}
