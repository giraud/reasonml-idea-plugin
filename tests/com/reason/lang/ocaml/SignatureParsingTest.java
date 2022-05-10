package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends OclParsingTestCase {
    public void test_let() {
        PsiLet e = first(letExpressions(parseCode("let x:int = 1")));

        PsiSignature signature = e.getSignature();
        assertEquals("int", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
    }

    public void test_OCamlBeforeDirective() {
        PsiVal e = first(valExpressions(parseCode("val bool_of_string_opt : string -> bool option\n(** This is a comment *)\n\n#if BS then\n#end")));

        PsiSignature signature = e.getSignature();
        assertEquals("string -> bool option", signature.asText(getLangProps()));
    }

    public void test_val() {
        PsiVal e = first(valExpressions(parseCode("val map : 'a option -> ('a -> 'b) -> 'b option")));

        PsiSignature signature = e.getSignature();
        List<PsiSignatureItem> items = signature.getItems();
        assertEquals("'a option -> ('a -> 'b) -> 'b option", signature.asText(getLangProps()));
        assertFalse(items.get(0).isOptional());
        assertFalse(items.get(1).isOptional());
        assertFalse(items.get(2).isOptional());
    }

    public void test_trimming() {
        PsiLet let = first(letExpressions(
                parseCode("let statelessComponent:\n  string ->\n  componentSpec(\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  )\n")));

        PsiSignature signature = let.getSignature();
        assertEquals("string -> componentSpec(stateless, stateless, noRetainedProps, noRetainedProps, actionless)", signature.asText(getLangProps()));
    }

    public void test_parsing_named_params() {
        PsiLet let = first(letExpressions(parseCode("let padding: v:length -> h:length -> rule")));

        PsiSignature signature = let.getSignature();
        assertEquals(3, signature.getItems().size());
        assertEquals("v:length -> h:length -> rule", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
        assertEquals("v", signature.getItems().get(0).getNamedParam().getName());
        assertFalse(signature.getItems().get(1).isOptional());
        assertEquals("h", signature.getItems().get(1).getNamedParam().getName());
    }

    public void test_optional_fun() {
        PsiLet let = first(letExpressions(parseCode("let x: int -> string option -> string = fun a  -> fun b  -> c")));

        PsiSignature signature = let.getSignature();
        assertEquals("int -> string option -> string", signature.asText(getLangProps()));

        List<PsiSignatureItem> items = let.getSignature().getItems();
        assertEquals("int", items.get(0).getText());
        assertFalse(items.get(0).isOptional());
        assertEquals("string option", items.get(1).getText());
        assertFalse(items.get(1).isOptional());
        assertEquals("string", items.get(2).getText());
        assertSize(3, items);
    }

    public void test_optional_fun_parameters() {
        PsiLet let = first(letExpressions(parseCode("let x a b ?(c= false)  ?(d= 1.)  = 3")));

        PsiFunction function = (PsiFunction) let.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());

        assertSize(4, parameters);
        assertFalse(parameters.get(0).isOptional());
        assertFalse(parameters.get(1).isOptional());
        assertTrue(parameters.get(2).isOptional());
        assertEquals("Dummy.x[c]", parameters.get(2).getQualifiedName());
        assertEquals("false", parameters.get(2).getDefaultValue().getText());
        assertTrue(parameters.get(3).isOptional());
        assertEquals("Dummy.x[d]", parameters.get(3).getQualifiedName());
        assertEquals("1.", parameters.get(3).getDefaultValue().getText());
    }

    public void test_optional_fun_parameters_typed() {
        PsiLet let = first(letExpressions(parseCode("let x (a : int) (b : string option) ?c:((c : bool)= false)  ?d:((d : float)=1.) = 3")));

        PsiFunction function = (PsiFunction) let.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());

        assertSize(4, parameters);
        assertFalse(parameters.get(0).isOptional());
        assertEquals("Dummy.x[a]", parameters.get(0).getQualifiedName());
        assertFalse(parameters.get(1).isOptional());
        assertEquals("Dummy.x[b]", parameters.get(1).getQualifiedName());
        assertTrue(parameters.get(2).isOptional());
        assertEquals("Dummy.x[c]", parameters.get(2).getQualifiedName());
        assertEquals("bool", parameters.get(2).getSignature().asText(getLangProps()));
        assertEquals("false", parameters.get(2).getDefaultValue().getText());
        assertTrue(parameters.get(3).isOptional());
        assertEquals("Dummy.x[d]", parameters.get(3).getQualifiedName());
        assertEquals("float", parameters.get(3).getSignature().asText(getLangProps()));
        assertEquals("1.", parameters.get(3).getDefaultValue().getText());
    }

    public void test_unitFunParameter() {
        PsiLet e = first(letExpressions(parseCode("let x (a : int) () = a")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());

        assertSize(2, parameters);
        assertEquals("(a : int)", parameters.get(0).getText());
        assertEquals("()", parameters.get(1).getText());
    }

    public void test_signature_items() {
        PsiLet e = first(letExpressions(parseCode("let createAction: < children : React.element; dispatch : ([ `Arity_1 of Redux.Actions.opaqueFsa ], unit) Js.Internal.fn; url : 'url > Js.t -> React.element;")));
        PsiSignature signature = e.getSignature();

        assertEquals(2, signature.getItems().size());
    }

    public void test_jsObject() {
        PsiLet e = first(letExpressions(parseCode("let x: < a: string; b: 'a > Js.t -> string")));
        PsiSignature signature = e.getSignature();

        assertEquals(2, signature.getItems().size());
        PsiSignatureItem jsObj = signature.getItems().get(0);
        List<PsiObjectField> fields = new ArrayList<>(PsiTreeUtil.findChildrenOfType(jsObj, PsiObjectField.class));
        assertSize(2, fields);
        assertEquals(fields.get(0).getName(), "a");
        assertEquals(fields.get(1).getName(), "b");
    }

    public void test_option() {
        PsiVal e = firstOfType(parseCode("val x: string array option"), PsiVal.class);

        PsiOption option = PsiTreeUtil.findChildOfType(e, PsiOption.class);
        assertEquals("string array option", option.getText());
    }

    public void test_option_named_params() {
        PsiExternal e = firstOfType(parseCode("external add : x:int option -> int = \"\""), PsiExternal.class);

        PsiOption option = PsiTreeUtil.findChildOfType(e, PsiOption.class);
        assertEquals("int option", option.getText());
    }
}
