package com.reason.lang.rescript;

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
public class LetParsingTest extends ResParsingTestCase {
    @Test
    public void test_constant() {
        PsiLet let = first(letExpressions(parseCode("let x = 1")));

        assertEquals("x", let.getName());
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    @Test
    public void test_function_let_binding() {
        PsiLet let = first(letExpressions(parseCode("let getAttributes = node => { node }")));

        assertTrue(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    @Test
    public void test_unit_function() {
        PsiLet e = first(letExpressions(parseCode("let x = () => 1")));

        assertTrue(e.isFunction());
        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("1", function.getBody().getText());
    }

    @Test
    public void test_binding() {
        PsiLet let = first(letExpressions(parseCode("let x = {\"u\": \"r\", \"l\": \"lr\"}")));

        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    @Test
    public void test_binding_with_jsx() {
        PsiFile file = parseCode("let make = p => { render: x => <div/> }");
        PsiElement[] children = file.getChildren();
        PsiElement element = PsiTreeUtil.nextLeaf(children[1]);

        assertNull(element);
        assertSize(1, expressions(file));
    }

    @Test
    public void test_scope_with_some() {
        PsiLet let = first(letExpressions(parseCode("let l = p => { switch a { | Some(a) => a\n () | None => () }\n Some(z) }")));

        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    @Test
    public void test_scope_with_LIdent() {
        PsiLet let = first(letExpressions(parseCode("let l = (p) => { Js.log(p)\n returnObj }")));

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
    }

    @Test
    public void test_local_scope() {
        PsiLet let = first(letExpressions(parseCode("let x = { let y = 1; y + 3 }")));

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
        assertNull(PsiTreeUtil.findChildOfType(binding, PsiRecord.class));
    }

    @Test
    public void test_record() {
        PsiLet let = first(letExpressions(parseCode("let typeScale = {one: 1.375, two: 1.0}")));

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
        PsiRecord record = PsiTreeUtil.findChildOfType(binding, PsiRecord.class);
        assertNotNull(record);
        assertSize(2, record.getFields());
    }

    @Test
    public void test_signature() {
        PsiLet let = first(letExpressions(parseCode("let combine: (style, style) => style = (a, b) => { }")));

        assertEquals("(style, style) => style", let.getSignature().getText());
        assertEquals("(a, b) => { }", let.getBinding().getText());
    }

    @Test
    public void test_signature_dot() {
        PsiLet let = first(letExpressions(parseCode("let x: M1.y => M2.z")));

        assertNull(PsiTreeUtil.findChildOfType(let, PsiFunction.class));
        assertEquals("M1.y => M2.z", let.getSignature().getText());
        List<PsiSignatureItem> items = new ArrayList<>(PsiTreeUtil.findChildrenOfType(let.getSignature(), PsiSignatureItem.class));
        assertEquals("M1.y", items.get(0).getText());
    }

    @Test
    public void test_signature_JsObject() {
        PsiLet let = first(letExpressions(parseCode("let x: {\"a\":string, \"b\":int} => unit")));

        assertEquals("{\"a\":string, \"b\":int} => unit", let.getSignature().getText());
        List<PsiObjectField> fields = new ArrayList<>(PsiTreeUtil.findChildrenOfType(let, PsiObjectField.class));
        assertEquals("\"a\":string", fields.get(0).getText());
        assertEquals("\"b\":int", fields.get(1).getText());
    }

    @Test
    public void test_rec() {
        PsiLet let = first(letExpressions(parseCode("let rec lx = x => x + 1")));

        assertTrue(let.isFunction());
        assertEquals("lx", let.getName());
    }

    @Test
    public void test_signatureB() {
        PsiLet e = firstOfType(parseCode("let watchUrl: (url => unit) => watcherID"), PsiLet.class);

        assertTrue(e.isFunction());
        assertEquals("watchUrl", e.getName());
        assertEquals("(url => unit) => watcherID", e.getSignature().getText());
    }

    @Test
    public void test_let_and_in_module() {
        FileBase file = parseCode("module M = { let f1 = x => x and f2 = y => y }");
        Collection<PsiNamedElement> es =
                PsiFileHelper.getModuleExpressions(file).iterator().next().getExpressions(pub, FILTER_LET);

        assertSize(2, es);
        assertEquals("f2 = y => y", second(es).getText());
    }

    @Test
    public void test_alias() {
        PsiLet e = first(letExpressions(parseCode("let x = M1.M2.y")));

        assertEquals("x", e.getName());
        assertEquals("M1.M2.y", e.getAlias());
    }

    @Test
    public void test_deconstruction() {
        PsiLet e = first(letExpressions(parseCode("let (a, b) = x")));

        assertTrue(e.isDeconstruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertInstanceOf(names.get(0), PsiLowerSymbol.class);
        assertEquals("b", names.get(1).getText());
        assertInstanceOf(names.get(1), PsiLowerSymbol.class);
    }

    @Test
    public void test_deconstruction_nested() { // belt_Map offset 2272
        PsiLet e = firstOfType(parseCode("let ((l, r), b) = Dict.split(~cmp, m.data, x)"), PsiLet.class);

        assertTrue(e.isDeconstruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(3, names);
        assertEquals("l", names.get(0).getText());
        assertInstanceOf(names.get(0), PsiLowerSymbol.class);
        assertEquals("r", names.get(1).getText());
        assertInstanceOf(names.get(1), PsiLowerSymbol.class);
        assertEquals("b", names.get(2).getText());
        assertInstanceOf(names.get(2), PsiLowerSymbol.class);
    }

    @Test
    public void test_deconstruction_braces() {
        PsiLet e = first(letExpressions(parseCode("let {a, b, _} = x")));

        assertEquals("x", e.getBinding().getText());
        assertTrue(e.isDeconstruction());
        List<PsiElement> names = e.getDeconstructedElements();
        assertSize(2, names);
        assertEquals("a", names.get(0).getText());
        assertInstanceOf(names.get(0), PsiLowerSymbol.class);
        assertEquals("b", names.get(1).getText());
        assertInstanceOf(names.get(1), PsiLowerSymbol.class);
    }

    @Test
    public void test_operator() {
        PsiLet e = first(letExpressions(parseCode("let \\\"/\": (path<'a, 'b> => 'c, 'd => path<'a, 'b>, 'd) => 'c")));

        assertEquals("\"/\"", e.getName());
        // ORSignature signature = e.getSignature();
        // assertEquals("(path('a, 'b) => 'c, 'd => path('a, 'b), 'd) => 'c",
        // signature.asString(myLanguage));
    }

    @Test
    public void test_function_record() {
        List<PsiLet> es = letExpressions(parseCode("let x = y(M.{i: 1}); let z=2"));

        assertSize(2, es);
        assertNull(PsiTreeUtil.findChildOfType(es.get(0), PsiScopedExpr.class));
        assertEquals("i", PsiTreeUtil.findChildOfType(es.get(0), PsiRecordField.class).getName());
        assertEquals("{i: 1}", PsiTreeUtil.findChildOfType(es.get(0), PsiRecord.class).getText());
    }

    @Test
    public void test_braces() {
        PsiLet e = first(letExpressions(parseCode("let x = p => { test ? { call(Some(a)) } : b }")));

        assertEquals("x", e.getName());
        assertTrue(e.isFunction());
        PsiFunction f = e.getFunction();
        assertEquals("{ test ? { call(Some(a)) } : b }", f.getBody().getText());
    }

    @Test
    public void test_chaining_1() {
        FileBase e = parseCode("let x = cond ? yes : no\n console(. any)");
        PsiLet l = firstOfType(e, PsiLet.class);
        PsiFunctionCall f = firstOfType(e, PsiFunctionCall.class);

        assertEquals("x", l.getName());
        assertEquals("cond ? yes : no", l.getBinding().getText());
        assertEquals("console", f.getName());
    }

    @Test
    public void test_chaining_call() {
        PsiLet e = first(letExpressions(parseCode("let x = Doc.y\n test()")));

        assertEquals("x", e.getName());
        assertEquals("Doc.y", e.getBinding().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105() {
        FileBase file = parseCode("let string = \"x\"");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("string", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105a() {
        FileBase file = parseCode("let int = 1");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("int", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/105
    @Test
    public void test_GH_105b() {
        FileBase file = parseCode("let bool = 1");
        PsiLet e = first(letExpressions(file));

        assertFalse(e.isFunction());
        assertEquals("bool", e.getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/278
    @Test
    public void test_GH_278() {
        PsiLet e = first(letExpressions(parseCode("let \\\"\\\\\" = Ext_path.combine"))); // let \"//" = ...

        assertEquals("\"\\\\\"", e.getName());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiComment.class));
    }
}
