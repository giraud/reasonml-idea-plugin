package com.reason.lang.rescript;

import com.intellij.psi.tree.*;
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
        RPsiLet let = firstOfType(parseCode("let x: int = 1"), RPsiLet.class);

        RPsiSignature signature = let.getSignature();
        assertEquals("int", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
    }

    @Test
    public void test_trimming() {
        RPsiLet let = firstOfType(parseCode("let statelessComponent:\n  string =>\n  componentSpec<\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  >\n"), RPsiLet.class);

        RPsiSignature signature = let.getSignature();
        assertEquals("string => componentSpec<stateless, stateless, noRetainedProps, noRetainedProps, actionless>", signature.asText(getLangProps()));
    }

    @Test
    public void test_parsing_named_params() {
        RPsiLet let = firstOfType(parseCode("let padding: (~v:length, ~h:length) => rule"), RPsiLet.class);

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
        RPsiLet let = firstOfType(parseCode("let x:int => option<string> => string = (a,b) => c"), RPsiLet.class);

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
        RPsiLet let = firstOfType(parseCode("let x = (a:Js.t, b:option<(. unit) => unit>, ~c:bool=false, ~d:float=?) => 3"), RPsiLet.class);
        assertNoParserError(let);

        RPsiFunction function = (RPsiFunction) let.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());

        RPsiParameterDeclaration p0 = parameters.get(0);
        assertFalse(p0.getSignature().getItems().get(0).isOptional());
        assertEquals("Js.t", p0.getSignature().getItems().get(0).getText());

        RPsiParameterDeclaration p1 = parameters.get(1);
        assertFalse(p1.getSignature().getItems().get(0).isOptional());
        assertEquals("option<(. unit) => unit>", p1.getSignature().getItems().get(0).getText());

        RPsiParameterDeclaration p2 = parameters.get(2);
        assertEquals("bool", p2.getSignature().asText(getLangProps()));
        assertTrue(p2.isOptional());
        assertEquals("false", p2.getDefaultValue().getText());

        RPsiParameterDeclaration p3 = parameters.get(3);
        assertEquals("float", p3.getSignature().asText(getLangProps()));
        assertTrue(p3.isOptional());
        assertEquals("?", p3.getDefaultValue().getText());
    }

    @Test
    public void test_unit_fun_parameter() {
        RPsiLet e = firstOfType(parseCode("let x = (~color=\"red\", ~radius=1, ()) => 1"), RPsiLet.class);

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
        RPsiExternal e = firstOfType(parseCode("external refToJsObj: reactRef => {..} = \"%identity\";"), RPsiExternal.class);

        RPsiSignature signature = e.getSignature();
        assertSize(2, ORUtil.findImmediateChildrenOfClass(e.getSignature(), RPsiSignatureItem.class));
        assertEquals("reactRef => {..}", signature.asText(getLangProps()));
    }

    @Test
    public void test_external_fun_2() {
        RPsiExternal e = firstOfType(parseCode("external requestAnimationFrame: (unit => string) => animationFrameID = \"\""), RPsiExternal.class);

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
        RPsiExternal e = firstOfType(parseCode("external e: option<show> = \"\""), RPsiExternal.class);

        RPsiSignatureItem sigItem = ORUtil.findImmediateChildrenOfClass(e.getSignature(), RPsiSignatureItem.class).iterator().next();
        assertEquals("option<show>", sigItem.asText(getLangProps()));
    }

    @Test // TODO
    public void test_default_optional() {
        RPsiLet let = firstOfType(parseCode("let createAction: (string, payload, ~meta: 'meta=?, unit) => opaqueFsa"), RPsiLet.class);
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
    public void test_closed_variant() {
        RPsiLet e = firstOfType(parseCode("let x: [< Css.Types.Length.t | Css.Types.Visibility.t ] => unit = _ => ()"), RPsiLet.class);
        assertNoParserError(e);

        List<IElementType> et = extractUpperSymbolTypes(e);
        assertDoesntContain(et, myTypes.A_VARIANT_NAME, myTypes.UIDENT);
        assertContainsElements(et, myTypes.A_MODULE_NAME);
    }

    @Test
    public void test_open_variant() {
        RPsiLet e = firstOfType(parseCode("let x: [< Css.Types.Length.t | Css.Types.Visibility.t ] => unit = _ => ()"), RPsiLet.class);
        assertNoParserError(e);

        List<IElementType> et = extractUpperSymbolTypes(e);
        assertDoesntContain(et, myTypes.A_VARIANT_NAME, myTypes.UIDENT);
        assertContainsElements(et, myTypes.A_MODULE_NAME);
    }

    @Test
    public void test_GH_399() {
        RPsiType e = firstOfType(parseCode("type t = React.Ref<Js.nullable<Dom.element>>"), RPsiType.class);
        assertNoParserError(e);

        assertNull(PsiTreeUtil.findChildOfType(e, RPsiTag.class));
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiUpperTagName.class));
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiLeafPropertyName.class));
    }

    @Test
    public void test_GH_399a() {
        RPsiLet e = firstOfType(parseCode("let fn = (domRef: React.ref<Js.nullable<Dom.element>>) => ()"), RPsiLet.class);
        assertNoParserError(e);

        assertNull(PsiTreeUtil.findChildOfType(e, RPsiTag.class));
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiUpperTagName.class));
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiLeafPropertyName.class));
    }
}
