package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.HMSignature;
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

        HMSignature signature = let.getSignature();
        assertTrue(signature.isMandatory(0));
    }

    public void testOptionalFun() {
        PsiLet let = first(letExpressions(parseCode("let x:int => option(string) => string = (a,b) => c")));

        HMSignature signature = let.getSignature();
        assertTrue(signature.isMandatory(0));
        assertFalse(signature.isMandatory(1));
    }

    public void testOptionalFunParameters() {
        PsiLet let = first(letExpressions(parseCode("let x = (a:int, b:option(string), c:bool=false, d:float=?) => 3", true)));

        PsiFunction function = (PsiFunction) let.getBinding().getFirstChild();
        List<PsiFunctionParameter> parameters = new ArrayList<>(function.getParameterList());

        assertTrue(parameters.get(0).getSignature().asHMSignature().isMandatory(0));
        assertFalse(parameters.get(1).getSignature().asHMSignature().isMandatory(0));
        assertFalse(parameters.get(2).getSignature().asHMSignature().isMandatory(0));
        assertEquals("bool=false", parameters.get(2).getSignature().asString());
        assertFalse(parameters.get(3).getSignature().asHMSignature().isMandatory(0));
        assertEquals("float=?", parameters.get(3).getSignature().asString());
    }

    public void testJsObject() {
        PsiType psiType = first(typeExpressions(parseCode("type props = { [@bs.optional] dangerouslySetInnerHTML: {. \"__html\": string} };")));

        PsiRecord record = (PsiRecord) psiType.getBinding().getFirstChild();
        List<PsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(1, fields.size());
        assertEquals("{. \"__html\": string}", fields.get(0).getSignature().asString());
    }
}
