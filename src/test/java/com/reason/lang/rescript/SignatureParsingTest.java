package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends ResParsingTestCase {
    @Test
    public void test_let() {
        RPsiLet let = first(letExpressions(parseCode("let x: int = 1")));

        RPsiSignature signature = let.getSignature();
        assertEquals("int", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
    }

    @Test
    public void test_trimming() {
        RPsiLet let = first(letExpressions(parseCode("let statelessComponent:\n  string =>\n  componentSpec<\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  >\n")));

        RPsiSignature signature = let.getSignature();
        assertEquals("string => componentSpec<stateless, stateless, noRetainedProps, noRetainedProps, actionless>", signature.asText(getLangProps()));
    }

    @Test
    public void test_parsing_named_params() {
        RPsiLet let = first(letExpressions(parseCode("let padding: (~v:length, ~h:length) => rule")));

        RPsiSignature signature = let.getSignature();
        assertEquals(3, signature.getItems().size());
        assertEquals("(~v:length, ~h:length) => rule", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
        assertEquals("v", signature.getItems().get(0).getName());
        assertFalse(signature.getItems().get(1).isOptional());
        assertEquals("h", signature.getItems().get(1).getName());
    }

    @Test
    public void test_optional_fun() {
        RPsiLet let = first(letExpressions(parseCode("let x:int => option<string> => string = (a,b) => c")));

        List<RPsiSignatureItem> items = let.getSignature().getItems();
        assertEquals("int", items.get(0).getText());
        assertFalse(items.get(0).isOptional());
        assertEquals("option<string>", items.get(1).getText());
        assertFalse(items.get(1).isOptional());
        assertEquals("string", items.get(2).getText());
        assertSize(3, items);
    }

    @Test
    public void test_optional_02() {
        RPsiLet e = firstOfType(parseCode("module Size: { let makeRecord: (~size: option<float> =?, unit) => t }"), RPsiLet.class);
        RPsiSignature es = e.getSignature();
        List<RPsiSignatureItem> esi = es.getItems();

        assertNoParserError(e);
        assertSize(3, esi);
        assertTrue(esi.get(0).isOptional());
        assertEquals("?", esi.get(0).getDefaultValue().getText());
        assertEquals("~size: option<float> =?", esi.get(0).asText(getLangProps()));
        assertFalse(esi.get(1).isOptional());
        assertEquals("unit", esi.get(1).getText());
    }

    @Test
    public void test_optional_fun_parameters() {
        RPsiLet let = first(letExpressions(parseCode("let x = (a:Js.t, b:option<string>, ~c:bool=false, ~d:float=?) => 3")));

        RPsiFunction function = (RPsiFunction) let.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());

        assertFalse(parameters.get(0).getSignature().getItems().get(0).isOptional());
        assertEquals("Js.t", parameters.get(0).getSignature().getItems().get(0).getText());
        assertFalse(parameters.get(1).getSignature().getItems().get(0).isOptional());
        assertEquals("bool", parameters.get(2).getSignature().asText(getLangProps()));
        assertTrue(parameters.get(2).isOptional());
        assertEquals("false", parameters.get(2).getDefaultValue().getText());
        assertEquals("float", parameters.get(3).getSignature().asText(getLangProps()));
        assertTrue(parameters.get(3).isOptional());
        assertEquals("?", parameters.get(3).getDefaultValue().getText());
    }

    @Test
    public void test_unit_fun_parameter() {
        RPsiLet e = first(letExpressions(parseCode("let x = (~color=\"red\", ~radius=1, ()) => 1")));

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());

        assertNoParserError(e);
        assertSize(3, parameters);
    }

    @Test
    public void test_jsObject() {
        RPsiType psiType = first(typeExpressions(parseCode("type props = {@optional dangerouslySetInnerHTML: {\"__html\": string}}")));

        RPsiRecord record = (RPsiRecord) psiType.getBinding().getFirstChild();
        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(1, fields.size());
        assertEquals("{\"__html\": string}", fields.get(0).getSignature().asText(getLangProps()));
    }

    @Test
    public void test_external_fun() {
        RPsiExternal e = first(externalExpressions(parseCode("external refToJsObj: reactRef => {..} = \"%identity\";")));

        RPsiSignature signature = e.getSignature();
        assertSize(2, ORUtil.findImmediateChildrenOfClass(e.getSignature(), RPsiSignatureItem.class));
        assertEquals("reactRef => {..}", signature.asText(getLangProps()));
    }

    @Test
    public void test_external_fun_2() {
        RPsiExternal e = first(externalExpressions(parseCode("external requestAnimationFrame: (unit => string) => animationFrameID = \"\"")));

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
        RPsiExternal e = first(externalExpressions(parseCode("external e: option<show> = \"\"")));

        RPsiSignatureItem sigItem = ORUtil.findImmediateChildrenOfClass(e.getSignature(), RPsiSignatureItem.class).iterator().next();
        assertEquals("option<show>", sigItem.asText(getLangProps()));
    }

    @Test
    public void test_default_optional() {
        RPsiLet let = first(letExpressions(parseCode("let createAction: (string, payload, ~meta: 'meta=?, unit) => opaqueFsa")));
        RPsiSignature signature = let.getSignature();
        // assertEquals("(string, payload, ~meta: 'meta=?, unit) => opaqueFsa",
        // signature.asString(getLangProps()));
    }

    @Test
    public void test_no_tag_01() {
        RPsiExternal e = firstOfType(parseCode("external make: (. Js.Dict.t<Js.Json.t>) => string"), RPsiExternal.class);
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiTag.class));
    }

    @Test
    public void test_no_tag_02() {
        RPsiExternal e = firstOfType(parseCode("external renderKeyframes: (. renderer, Js.Dict.t<Js.Json.t>) => string"), RPsiExternal.class);
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiTag.class));
    }

    @Test
    public void test_no_tag_03() {
        RPsiType e = firstOfType(parseCode("type t = [ | #none | #areas(list<string>)]"), RPsiType.class);
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiTag.class));
    }

    @Test
    public void test_GH_399() {
        RPsiType e = firstOfType(parseCode("type t = React.Ref<Js.nullable<Dom.element>>"), RPsiType.class);

        assertNull(PsiTreeUtil.findChildOfType(e, RPsiTag.class));
    }
}
