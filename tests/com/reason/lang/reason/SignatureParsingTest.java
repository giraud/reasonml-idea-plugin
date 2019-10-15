package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.ocaml.OclLanguage;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends BaseParsingTestCase {
    public SignatureParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testMandatoryVal() {
        PsiLet let = first(letExpressions(parseCode("let x:int = 1")));

        ORSignature signature = let.getORSignature();
        assertEquals("int", signature.asString(RmlLanguage.INSTANCE));
        assertTrue(signature.isMandatory(0));
    }

    public void testTrimming() {
        PsiLet let = first(letExpressions(parseCode("let statelessComponent:\n  string =>\n  componentSpec(\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  );\n")));

        PsiSignature signature = let.getPsiSignature();
        assertEquals("string => componentSpec(stateless, stateless, noRetainedProps, noRetainedProps, actionless)", signature.asString(RmlLanguage.INSTANCE));
    }

    public void testParsingRml() {
        PsiLet let = first(letExpressions(parseCode("let padding: (~v:length, ~h:length) => rule;")));

        ORSignature signature = let.getORSignature();
        assertEquals(3, signature.getTypes().length);
        assertEquals("(~v:length, ~h:length) => rule", signature.asString(RmlLanguage.INSTANCE));
        assertTrue(signature.isMandatory(0));
        assertTrue(signature.isMandatory(1));
    }

    public void testOptionalFun() {
        PsiLet let = first(letExpressions(parseCode("let x:int => option(string) => string = (a,b) => c")));

        ORSignature signature = let.getORSignature();
        assertTrue(signature.isMandatory(0));
        assertFalse(signature.isMandatory(1));
    }

    public void testOptionalFunParameters() {
        PsiLet let = first(letExpressions(parseCode("let x = (a:int, b:option(string), c:bool=false, d:float=?) => 3", true)));

        PsiFunction function = (PsiFunction) let.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());

        assertTrue(parameters.get(0).getPsiSignature().asHMSignature().isMandatory(0));
        assertFalse(parameters.get(1).getPsiSignature().asHMSignature().isMandatory(0));
//        assertFalse(parameters.get(2).getPsiSignature().asHMSignature().isMandatory(0));
        assertEquals("bool", parameters.get(2).getPsiSignature().asString(RmlLanguage.INSTANCE));
//        assertFalse(parameters.get(3).getPsiSignature().asHMSignature().isMandatory(0));
        assertEquals("float", parameters.get(3).getPsiSignature().asString(RmlLanguage.INSTANCE));
    }

    public void testUnitFunParameter() {
        PsiLet e = first(letExpressions(parseCode("let x = (~color=\"red\", ~radius=1, ()) => 1")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());

        assertSize(3, parameters);
    }

    public void testJsObject() {
        PsiType psiType = first(typeExpressions(parseCode("type props = { [@bs.optional] dangerouslySetInnerHTML: {. \"__html\": string} };")));

        PsiRecord record = (PsiRecord) psiType.getBinding().getFirstChild();
        List<PsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(1, fields.size());
        assertEquals("{. \"__html\": string}", fields.get(0).getPsiSignature().asString(RmlLanguage.INSTANCE));
    }

    public void testExternalFun() {
        PsiExternal e = first(externalExpressions(parseCode("external refToJsObj : reactRef => Js.t({..}) = \"%identity\";")));

        ORSignature signature = e.getORSignature();
        assertSize(2, ORUtil.findImmediateChildrenOfClass(e.getPsiSignature(), PsiSignatureItem.class));
        assertEquals("reactRef => Js.t({..})", signature.asString(RmlLanguage.INSTANCE));
    }

    public void testExternalFun2() {
        PsiExternal e = first(externalExpressions(parseCode("external requestAnimationFrame: (unit => unit) => animationFrameID = \"\";")));

        ORSignature signature = e.getORSignature();
        List<PsiSignatureItem> signatureItems = ORUtil.findImmediateChildrenOfClass(e.getPsiSignature(), PsiSignatureItem.class);
        assertSize(2, signatureItems);
        assertEquals("unit => unit", signatureItems.iterator().next().getText());
//TODO        assertEquals("(unit => unit) => animationFrameID", signature.asString(RmlLanguage.INSTANCE));
    }

    public void testOption() {
        PsiExternal e = first(externalExpressions(parseCode("external e : option(show) = \"\";")));

        PsiSignatureItem sigItem = ORUtil.findImmediateChildrenOfClass(e.getPsiSignature(), PsiSignatureItem.class).iterator().next();
        assertEquals("option(show)", sigItem.asText(RmlLanguage.INSTANCE));
        assertEquals("show option", sigItem.asText(OclLanguage.INSTANCE));
    }

    public void testDefaultOptional() {
        PsiLet let = first(letExpressions(parseCode("let createAction: (string, payload, ~meta: 'meta=?, unit) => opaqueFsa;")));
        ORSignature signature = let.getORSignature();
        assertEquals("(string, payload, ~meta: 'meta=?, unit) => opaqueFsa", signature.asString(RmlLanguage.INSTANCE));
    }

}
