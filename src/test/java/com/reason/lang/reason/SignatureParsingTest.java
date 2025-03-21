package com.reason.lang.reason;

import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends RmlParsingTestCase {
    @Test
    public void test_let() {
        RPsiLet let = firstOfType(parseCode("let x:int = 1"), RPsiLet.class);

        RPsiSignature signature = let.getSignature();
        assertEquals("int", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
    }

    @Test
    public void test_trimming() {
        RPsiLet let = firstOfType(parseCode("let statelessComponent:\n  string =>\n  componentSpec(\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  );\n"), RPsiLet.class);

        RPsiSignature signature = let.getSignature();
        assertEquals("string => componentSpec(stateless, stateless, noRetainedProps, noRetainedProps, actionless)", signature.asText(getLangProps()));
    }

    @Test
    public void test_parsing_named_params() {
        RPsiLet let = firstOfType(parseCode("let padding: (~v:length, ~h:length) => rule;"), RPsiLet.class);

        RPsiSignature signature = let.getSignature();
        assertEquals(3, signature.getItems().size());
        assertEquals("(~v:length, ~h:length) => rule", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
        assertEquals("v", signature.getItems().get(0).getName());
        assertFalse(signature.getItems().get(1).isOptional());
        assertEquals("h", signature.getItems().get(1).getName());
        assertEquals("rule", signature.getItems().get(2).getText());
    }

    @Test
    public void test_optional_fun() {
        RPsiLet let = firstOfType(parseCode("let x:int => option(string) => string = (a,b) => c"), RPsiLet.class);

        List<RPsiSignatureItem> items = let.getSignature().getItems();
        assertEquals("int", items.get(0).getText());
        assertFalse(items.get(0).isOptional());
        assertEquals("option(string)", items.get(1).getText());
        assertFalse(items.get(1).isOptional());
        assertEquals("string", items.get(2).getText());
        assertSize(3, items);
    }

    @Test
    public void test_optional_fun_parameters() {
        RPsiLet let = firstOfType(parseCode("let x = (a:Js.t, b:option((. unit) => unit), ~c:bool=false, ~d:float=?) => 3"), RPsiLet.class);

        RPsiFunction function = (RPsiFunction) let.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());

        assertSize(4, parameters);
        assertFalse(parameters.get(0).getSignature().getItems().get(0).isOptional());
        assertEquals("Js.t", parameters.get(0).getSignature().getItems().get(0).getText());
        assertFalse(parameters.get(1).getSignature().getItems().get(0).isOptional());
        assertEquals("option((. unit) => unit)", parameters.get(1).getSignature().getItems().get(0).getText());
        assertTrue(parameters.get(2).isOptional());
        assertEquals("bool", parameters.get(2).getSignature().asText(getLangProps()));
        assertEquals("false", parameters.get(2).getDefaultValue().getText());
        assertTrue(parameters.get(3).isOptional());
        assertEquals("float", parameters.get(3).getSignature().asText(getLangProps()));
        assertEquals("?", parameters.get(3).getDefaultValue().getText());
    }

    @Test
    public void test_optional_02() {
        RPsiLet let = firstOfType(parseCode("module Size: { let makeRecord: (~size: option(float)=?, unit) => t; };"), RPsiLet.class);

        RPsiSignature s = let.getSignature();
        List<RPsiSignatureItem> si = s.getItems();

        assertSize(3, si);
        assertTrue(si.get(0).isOptional());
        assertEquals("?", si.get(0).getDefaultValue().getText());
        assertEquals("~size: option(float)=?", si.get(0).asText(getLangProps()));
        assertFalse(si.get(1).isOptional());
        assertEquals("unit", si.get(1).getText());
    }

    @Test
    public void test_unit_fun_parameter() {
        RPsiLet e = firstOfType(parseCode("let x = (~color=\"red\", ~radius=1, ()) => 1"), RPsiLet.class);

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());

        assertSize(3, parameters);
    }

    @Test
    public void test_jsObject() {
        RPsiType psiType = first(typeExpressions(parseCode("type props = { [@bs.optional] dangerouslySetInnerHTML: {. \"__html\": string} };")));

        RPsiRecord record = (RPsiRecord) psiType.getBinding().getFirstChild();
        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(1, fields.size());
        assertEquals("{. \"__html\": string}", fields.get(0).getSignature().asText(getLangProps()));
    }

    @Test
    public void test_external_fun() {
        RPsiExternal e = firstOfType(parseCode("external refToJsObj : reactRef => Js.t({..}) = \"%identity\";"), RPsiExternal.class);

        RPsiSignature signature = e.getSignature();
        assertSize(2, ORUtil.findImmediateChildrenOfClass(e.getSignature(), RPsiSignatureItem.class));
        assertEquals("reactRef => Js.t({..})", signature.asText(getLangProps()));
    }

    @Test
    public void test_external_fun_2() {
        RPsiExternal e = firstOfType(parseCode("external requestAnimationFrame: (unit => string) => animationFrameID = \"\";"), RPsiExternal.class);

        RPsiSignature signature = e.getSignature();
        List<RPsiSignatureItem> signatureItems = signature.getItems();
        assertEquals("unit", signatureItems.get(0).getText());
        assertEquals("string", signatureItems.get(1).getText());
        assertEquals("animationFrameID", signatureItems.get(2).getText());
        assertSize(3, signatureItems);
    }

    @Test
    public void test_dot() {
        RPsiExternal e = firstOfType(parseCode("external getPlatformInformation: (. store) => platform = \"\""), RPsiExternal.class);

        List<RPsiSignatureItem> items = e.getSignature().getItems();
        assertSize(2, items);
        assertEquals("store", items.get(0).getText());
        assertEquals("platform", items.get(1).getText());
    }

    @Test
    public void test_option() {
        RPsiExternal e = firstOfType(parseCode("external e : option(show) = \"\";"), RPsiExternal.class);

        RPsiSignatureItem sigItem = ORUtil.findImmediateChildrenOfClass(e.getSignature(), RPsiSignatureItem.class).iterator().next();
        assertEquals("option(show)", sigItem.asText(getLangProps()));
    }

    @Test
    public void test_default_optional() {
        RPsiLet e = firstOfType(parseCode("let createAction: (string, payload, ~meta: 'meta=?, unit) => opaqueFsa;"), RPsiLet.class);
        RPsiSignature es = e.getSignature();
        assertEquals("(string, payload, ~meta: 'meta=?, unit) => opaqueFsa", es.asText(getLangProps()));
        RPsiSignatureItem esi = es.getItems().get(2);
        assertEquals("meta", esi.getName());
        assertEquals("?", esi.getDefaultValue().getText());
    }

    @Test
    public void test_react() {
        RPsiExternal e = firstOfType(parseCode("external useState: ([@bs.uncurry] (unit => 'state)) => ('state, (. ('state => 'state)) => unit) = \"useState\";"), RPsiExternal.class);

        assertEquals("useState", e.getExternalName());
        assertEquals("([@bs.uncurry] (unit => 'state)) => ('state, (. ('state => 'state)) => unit)", e.getSignature().getText());
        assertEmpty(PsiTreeUtil.findChildrenOfType(e, RPsiFunction.class));
        List<RPsiSignatureItem> signatureItems = e.getSignature().getItems();
        assertEquals("[@bs.uncurry] (unit => 'state)", signatureItems.get(0).getText());
        assertEquals("('state, (. ('state => 'state)) => unit)", signatureItems.get(1).getText());
    }

    @Test
    public void test_closed_variant() {
        RPsiLet e = firstOfType(parseCode("let x: [< Css.Types.Length.t | Css.Types.Visibility.t ] => unit = _ => ();"), RPsiLet.class);

        List<IElementType> et = extractUpperSymbolTypes(e);
        assertDoesntContain(et, myTypes.A_VARIANT_NAME, myTypes.UIDENT);
        assertContainsElements(et, myTypes.A_MODULE_NAME);
    }

    @Test
    public void test_open_variant() {
        RPsiLet e = firstOfType(parseCode("let x: [> Css.Types.Length.t | Css.Types.Visibility.t ] => unit = _ => ();"), RPsiLet.class);

        List<IElementType> et = extractUpperSymbolTypes(e);
        assertDoesntContain(et, myTypes.A_VARIANT_NAME, myTypes.UIDENT);
        assertContainsElements(et, myTypes.A_MODULE_NAME);
    }
}
