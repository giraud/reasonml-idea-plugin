package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.psi.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends BaseParsingTestCase {
    public SignatureParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testLet() {
        PsiLet e = first(letExpressions(parseCode("let x:int = 1")));

        ORSignature signature = e.getHMSignature();
        assertEquals("int", signature.asString(OclLanguage.INSTANCE));
        assertTrue(signature.isMandatory(0));
    }

    public void testVal() {
        PsiVal e = first(valExpressions(parseCode("val map : 'a option -> ('a -> 'b) -> 'b option")));

        ORSignature signature = e.getHMSignature();
        assertEquals("'a option -> ('a -> 'b) -> 'b option", signature.asString(OclLanguage.INSTANCE));
        assertFalse(signature.isMandatory(0));
        assertTrue(signature.isMandatory(1));
        assertFalse(signature.isMandatory(2));
    }

    public void testTrimming() {
        PsiLet let = first(letExpressions(parseCode("let statelessComponent:\n  string ->\n  componentSpec(\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  );\n")));

        PsiSignature signature = let.getSignature();
        assertEquals("string -> componentSpec(stateless, stateless, noRetainedProps, noRetainedProps, actionless)", signature.asString(OclLanguage.INSTANCE));
    }

    public void testParsingRml() {
        PsiLet let = first(letExpressions(parseCode("let padding: v:length -> h:length -> rule")));

        ORSignature signature = let.getHMSignature();
        assertEquals(3, signature.getTypes().length);
        assertEquals("v:length -> h:length -> rule", signature.asString(OclLanguage.INSTANCE));
        assertTrue(signature.isMandatory(0));
        assertTrue(signature.isMandatory(1));
    }

    public void testOptionalFun() {
        PsiLet let = first(letExpressions(parseCode("let x: int -> string option -> string = fun a  -> fun b  -> c")));

        ORSignature signature = let.getHMSignature();
        assertEquals(3, signature.getTypes().length);
        assertEquals("int -> string option -> string", signature.asString(OclLanguage.INSTANCE));
        assertTrue(signature.isMandatory(0));
        assertFalse(signature.isMandatory(1));
    }

    public void testOptionalFunParameters() {
        PsiLet let = first(letExpressions(parseCode("let x (a : int) (b : string option) (c : bool) (d : float) = 3")));

        PsiFunction function = (PsiFunction) let.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameterList());

        assertTrue(parameters.get(0).getSignature().asHMSignature().isMandatory(0));
        assertFalse(parameters.get(1).getSignature().asHMSignature().isMandatory(0));
        assertTrue(parameters.get(2).getSignature().asHMSignature().isMandatory(0));
        assertTrue(parameters.get(3).getSignature().asHMSignature().isMandatory(0));
    }

}
