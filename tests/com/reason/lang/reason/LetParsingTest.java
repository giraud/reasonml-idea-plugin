package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

import static com.reason.lang.core.ExpressionFilterConstants.*;
import static com.reason.lang.core.psi.impl.ExpressionScope.*;

@SuppressWarnings("ConstantConditions")
public class LetParsingTest extends RmlParsingTestCase {
    @Test
    public void test_constant() {
        RPsiLet let = first(letExpressions(parseCode("let x = 1;")));

        assertEquals("x", let.getName());
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_function_let_binding() {
        RPsiLet let = first(letExpressions(parseCode("let getAttributes = node => { node; };")));

        assertTrue(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_unit_function() {
        RPsiLet e = first(letExpressions(parseCode("let x = () => 1;")));

        assertTrue(e.isFunction());
        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        assertEquals("1", function.getBody().getText());
    }

    @Test
    public void test_binding() {
        RPsiLet let = first(letExpressions(parseCode("let x = {\"u\": \"r\", \"l\": \"lr\"};")));
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_binding_with_jsx() {
        PsiFile file = parseCode("let make = p => { render: x => { <div/>; } }");
        PsiElement[] children = file.getChildren();
        PsiElement element = PsiTreeUtil.nextLeaf(children[1]);

        assertNull(element);
        assertSize(1, expressions(file));
    }

    @Test
    public void test_scope_with_some() {
        RPsiLet let = first(letExpressions(parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };")));

        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class)));
    }

    @Test
    public void test_scope_with_LIdent() {
        RPsiLet let = first(letExpressions(parseCode("let l = (p) => { Js.log(p); returnObj; };")));

        RPsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class));
        assertNotNull(binding);
    }

    @Test
    public void test_local_scope() {
        RPsiLet let = first(letExpressions(parseCode("let x = { let y = 1; y + 3; }")));

        RPsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class));
        assertNotNull(binding);
        assertNull(PsiTreeUtil.findChildOfType(binding, RPsiRecord.class));
    }

    @Test
    public void test_record() {
        RPsiLet let = first(letExpressions(parseCode("let typeScale = {one: 1.375, two: 1.0};")));

        RPsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, RPsiLetBinding.class));
        RPsiRecord record = PsiTreeUtil.findChildOfType(binding, RPsiRecord.class);
        assertSize(2, record.getFields());
    }

    @Test
    public void test_signature() {
        RPsiLet let = first(letExpressions(parseCode("let combine: (style, style) => style = (a, b) => { };")));

        assertEquals("(style, style) => style", let.getSignature().getText());
        assertEquals("(a, b) => { }", let.getBinding().getText());
    }

    @Test
    public void test_signature_dot() {
        RPsiLet let = first(letExpressions(parseCode("let x: M1.y => M2.z;")));

        assertNull(PsiTreeUtil.findChildOfType(let, RPsiFunction.class));
        assertEquals("M1.y => M2.z", let.getSignature().getText());
        List<RPsiSignatureItem> items = new ArrayList<>(PsiTreeUtil.findChildrenOfType(let.getSignature(), RPsiSignatureItem.class));
        assertEquals("M1.y", items.get(0).getText());
    }

    @Test
    public void test_signature_JsObject() {
        RPsiLet let = first(letExpressions(parseCode("let x: {. a:string, b:int } => unit;")));

        assertEquals("{. a:string, b:int } => unit", let.getSignature().getText());
        List<RPsiObjectField> fields = new ArrayList<>(PsiTreeUtil.findChildrenOfType(let, RPsiObjectField.class));
        assertEquals("a:string", fields.get(0).getText());
        assertEquals("b:int", fields.get(1).getText());
    }

    @Test
    public void test_rec() {
        RPsiLet let = first(letExpressions(parseCode("let rec lx = x => x + 1")));

        assertTrue(let.isFunction());
        assertEquals("lx", let.getName());
    }

    @Test
    public void test_signatureB() {
        FileBase file = parseCode("let watchUrl: (url => unit) => watcherID;");
        RPsiLet e = first(letExpressions(file));

        assertTrue(e.isFunction());
        assertEquals("watchUrl", e.getName());
        assertEquals("(url => unit) => watcherID", e.getSignature().getText());
    }

    @Test
    public void test_let_and_in_module() {
        FileBase file = parseCode("module M = { let f1 = x => x and f2 = y => y; };");
        Collection<PsiNamedElement> es = PsiFileHelper.getModuleExpressions(file).iterator().next().getExpressions(pub, FILTER_LET);

        assertSize(2, es);
        assertEquals("f2 = y => y", second(es).getText());
    }

    @Test
    public void test_alias() {
        RPsiLet e = first(letExpressions(parseCode("let x = M1.M2.y;")));

        assertEquals("x", e.getName());
        assertEquals("M1.M2.y", e.getAlias());
    }

    @Test
    public void test_variant() {
        RPsiLet e = first(letExpressions(parseCode("let x = MyVar;")));

        assertEquals("x", e.getName());
        assertEquals("MyVar", e.getAlias());
        assertEquals(RmlTypes.INSTANCE.A_VARIANT_NAME, e.getBinding().getFirstChild().getNode().getElementType());
    }

    @Test
    public void test_deconstruction() {
        RPsiLet e = first(letExpressions(parseCode("let (a, b) = x;")));

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
        RPsiLet e = firstOfType(parseCode("let ((l, r), b) = Dict.split(~cmp, m.data, x);"), RPsiLet.class);

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
        RPsiLet e = first(letExpressions(parseCode("let {a, b, _} = x;")));

        assertEquals("x", e.getBinding().getText());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertInstanceOf(names.get(0), RPsiLowerSymbol.class);
        assertEquals("b", names.get(1).getText());
        assertInstanceOf(names.get(1), RPsiLowerSymbol.class);
    }

    @Test
    public void test_operator() {
        RPsiLet e = first(letExpressions(parseCode("let (/): (path('a, 'b) => 'c, 'd => path('a, 'b), 'd) => 'c;")));

        assertEquals("(/)", e.getName());
        // ORSignature signature = e.getSignature();
        // assertEquals("(path('a, 'b) => 'c, 'd => path('a, 'b), 'd) => 'c",
        // signature.asString(myLanguage));
    }

    @Test
    public void test_private() {
        RPsiLet e = first(letExpressions(parseCode("let x%private = 1;")));

        assertEquals("x", e.getName());
        assertTrue(e.isPrivate());
    }

    @Test
    public void test_function_record() {
        List<RPsiLet> es = letExpressions(parseCode("let x = y(M.{i: 1}); let z=2;"));

        assertSize(2, es);
        assertNull(PsiTreeUtil.findChildOfType(es.get(0), RPsiScopedExpr.class));
        assertEquals("i", PsiTreeUtil.findChildOfType(es.get(0), RPsiRecordField.class).getName());
        assertEquals("{i: 1}", PsiTreeUtil.findChildOfType(es.get(0), RPsiRecord.class).getText());
    }

    @Test
    public void test_braces() {
        RPsiLet e = first(letExpressions(parseCode("let x = p => { test ? { call(Some(a)); } : b };")));

        assertEquals("x", e.getName());
        assertTrue(e.isFunction());
        RPsiFunction f = e.getFunction();
        assertEquals("{ test ? { call(Some(a)); } : b }", f.getBody().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105() {
        FileBase file = parseCode("let string = \"x\"");
        RPsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("string", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105a() {
        FileBase file = parseCode("let int = 1");
        RPsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("int", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105b() {
        FileBase file = parseCode("let bool = 1");
        RPsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("bool", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/278
    @Test
    public void test_GH_278() {
        RPsiLet e = first(letExpressions(parseCode("let (/\\/) = Ext_path.combine")));

        assertEquals("(/\\/)", e.getName());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiComment.class));
    }
}
