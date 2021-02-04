package com.reason.lang.napkin;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends NsParsingTestCase {
    public void test_mandatoryVal() {
        PsiLet let = first(letExpressions(parseCode("let x:int = 1")));

        PsiSignature signature = let.getSignature();
        assertEquals("int", signature.asText(myLanguage));
        assertFalse(signature.getItems().get(0).isOptional());
    }
    /*
    public void test_trimming() {
        PsiLet let = first(letExpressions(parseCode("let statelessComponent:\n  string =>\n  componentSpec(\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  )\n")));

        PsiSignature signature = let.getSignature();
        assertEquals("string => componentSpec(stateless, stateless, noRetainedProps, noRetainedProps, actionless)", signature.asString(myLanguage));
    }

    public void test_named_param() {
        PsiLet let = first(letExpressions(parseCode("let padding: (~v:length, ~h:length) => rule")));

        PsiSignature signature = let.getSignature();
        List<PsiSignatureItem> items = new ArrayList<>(PsiTreeUtil.findChildrenOfType(signature, PsiSignatureItem.class));
        assertEquals(5, items.size());
        assertEquals("(~v:length, ~h:length) => rule", signature.getText());
    }

    public void test_optionalFun() {
        PsiLet let = first(letExpressions(parseCode("let x:int => option<string> => string = (a,b) => c")));

        List<PsiSignatureItem> items = let.getSignature().getItems();
        assertTrue(items.get(0).isOptional());
        assertFalse(items.get(1).isOptional());
    }

    public void test_optionalFunParameters() {
        PsiLet let = first(letExpressions(parseCode("let x = (a:int, b:option<string>, c:bool=false, d:float=?) => 3")));

        PsiFunction function = (PsiFunction) let.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());

        assertTrue(parameters.get(0).getSignature().getItems().get(0).isOptional());
        assertFalse(parameters.get(1).getSignature().getItems().get(0).isOptional());
        //        assertFalse(parameters.get(2).getSignature().asHMSignature().isMandatory(0));
        assertEquals("bool", parameters.get(2).getSignature().asString(myLanguage));
        //        assertFalse(parameters.get(3).getSignature().asHMSignature().isMandatory(0));
        assertEquals("float", parameters.get(3).getSignature().asString(myLanguage));
    }

    public void test_unitFunParameter() {
        PsiLet e = first(letExpressions(parseCode("let x = (~color=\"red\", ~radius=1, ()) => 1")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());

        assertSize(3, parameters);
    }

    public void test_jsObject() {
        PsiType psiType = first(typeExpressions(parseCode("type props = { @bs.optional dangerouslySetInnerHTML: {. \"__html\": string} }")));

        PsiRecord record = (PsiRecord) psiType.getBinding().getFirstChild();
        List<PsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(1, fields.size());
        assertEquals("{. \"__html\": string}", fields.get(0).getSignature().getText());
    }

    public void test_externalFun() {
        PsiExternal e = first(externalExpressions(parseCode("external refToJsObj : reactRef => {..} = \"%identity\"")));

        PsiSignature signature = e.getSignature();
        assertSize(2, ORUtil.findImmediateChildrenOfClass(e.getSignature(), PsiSignatureItem.class));
        assertEquals("reactRef => {..}", signature.asString(myLanguage));
    }

    public void test_externalFun2() {
        PsiExternal e = first(externalExpressions(parseCode("external requestAnimationFrame: (unit => unit) => animationFrameID = \"\"")));

        PsiSignature signature = e.getSignature();
        Collection<PsiSignatureItem> signatureItems = PsiTreeUtil.findChildrenOfType(e.getSignature(), PsiSignatureItem.class);
        assertSize(3, signatureItems);
        Iterator<PsiSignatureItem> itSig = signatureItems.iterator();
        assertEquals("unit", itSig.next().getText());
        assertEquals("unit", itSig.next().getText());
        assertEquals("animationFrameID", itSig.next().getText());
    }

    public void test_option() {
        PsiExternal e = first(externalExpressions(parseCode("external e : option<show> = \"\"")));

        PsiSignatureItem sigItem = ORUtil.findImmediateChildrenOfClass(e.getSignature(), PsiSignatureItem.class).iterator().next();
        assertEquals("option<show>", sigItem.asText(myLanguage));
    }

    // public void test_defaultOptional() {
    //    PsiLet let = first(letExpressions(parseCode("let createAction: (string, payload, ~meta:
    // 'meta=?, unit) => opaqueFsa")));
    //    PsiSignature signature = let.getSignature();
    //    assertEquals("(string, payload, ~meta: 'meta=?, unit) => opaqueFsa",
    // signature.asString(myLanguage));
    // }

    public void test_GH_275b() {
        PsiSwitch e = firstOfType(parseCode("switch (a, b) { | (Some(a'), Some(b')) => let _ = { switch (x) { | None => None } } }"), PsiSwitch.class);

        assertEquals("switch (a, b) { | (Some(a'), Some(b')) => let _ = { switch (x) { | None => None } } }", e.getText());
        assertEquals("let _ = { switch (x) { | None => None } }", e.getPatterns().get(0).getBody().getText());
    }
    */
}
