package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORSignature;
import com.reason.lang.core.psi.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends BaseParsingTestCase {
    public SignatureParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testMandatoryVal() {
        PsiLet let = first(letExpressions(parseCode("let x:int = 1")));

        ORSignature signature = let.getHMSignature();
        assertEquals("int", signature.asString(RmlLanguage.INSTANCE));
        assertTrue(signature.isMandatory(0));
    }

    public void testTrimming() {
        PsiLet let = first(letExpressions(parseCode("let statelessComponent:\n  string =>\n  componentSpec(\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  );\n")));

        PsiSignature signature = let.getSignature();
        assertEquals("string => componentSpec(stateless, stateless, noRetainedProps, noRetainedProps, actionless)", signature.asString(RmlLanguage.INSTANCE));
    }

    public void testParsingRml() {
        PsiLet let = first(letExpressions(parseCode("let padding: (~v:length, ~h:length) => rule;")));

        ORSignature signature = let.getHMSignature();
        assertEquals(3, signature.getTypes().length);
        assertEquals("(~v:length, ~h:length) => rule", signature.asString(RmlLanguage.INSTANCE));
        assertTrue(signature.isMandatory(0));
        assertTrue(signature.isMandatory(1));
    }

    public void testOptionalFun() {
        PsiLet let = first(letExpressions(parseCode("let x:int => option(string) => string = (a,b) => c")));

        ORSignature signature = let.getHMSignature();
        assertTrue(signature.isMandatory(0));
        assertFalse(signature.isMandatory(1));
    }

    public void testOptionalFunParameters() {
        PsiLet let = first(letExpressions(parseCode("let x = (a:int, b:option(string), c:bool=false, d:float=?) => 3")));

        PsiFunction function = (PsiFunction) let.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameterList());

        assertTrue(parameters.get(0).getSignature().asHMSignature().isMandatory(0));
        assertFalse(parameters.get(1).getSignature().asHMSignature().isMandatory(0));
        assertFalse(parameters.get(2).getSignature().asHMSignature().isMandatory(0));
        assertEquals("bool=false", parameters.get(2).getSignature().asString(RmlLanguage.INSTANCE));
        assertFalse(parameters.get(3).getSignature().asHMSignature().isMandatory(0));
        assertEquals("float=?", parameters.get(3).getSignature().asString(RmlLanguage.INSTANCE));
    }

    public void testJsObject() {
        PsiType psiType = first(typeExpressions(parseCode("type props = { [@bs.optional] dangerouslySetInnerHTML: {. \"__html\": string} };")));

        PsiRecord record = (PsiRecord) psiType.getBinding().getFirstChild();
        List<PsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(1, fields.size());
        assertEquals("{. \"__html\": string}", fields.get(0).getSignature().asString(RmlLanguage.INSTANCE));
    }
}
