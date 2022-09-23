package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends OclParsingTestCase {
    @Test
    public void test_let() {
        RPsiLet e = first(letExpressions(parseCode("let x:int = 1")));

        RPsiSignature signature = e.getSignature();
        assertEquals("int", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
    }

    @Test
    public void test_OCamlBeforeDirective() {
        RPsiVal e = first(valExpressions(parseCode("val bool_of_string_opt : string -> bool option\n(** This is a comment *)\n\n#if BS then\n#end")));

        RPsiSignature signature = e.getSignature();
        assertEquals("string -> bool option", signature.asText(getLangProps()));
    }

    @Test
    public void test_val() {
        RPsiVal e = first(valExpressions(parseCode("val map : 'a option -> ('a -> 'b) -> 'b option")));

        RPsiSignature signature = e.getSignature();
        List<RPsiSignatureItem> items = signature.getItems();
        assertEquals("'a option -> ('a -> 'b) -> 'b option", signature.asText(getLangProps()));
        assertFalse(items.get(0).isOptional());
        assertFalse(items.get(1).isOptional());
        assertFalse(items.get(2).isOptional());
    }

    @Test
    public void test_trimming() {
        RPsiLet let = first(letExpressions(
                parseCode("let statelessComponent:\n  string ->\n  componentSpec(\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  )\n")));

        RPsiSignature signature = let.getSignature();
        assertEquals("string -> componentSpec(stateless, stateless, noRetainedProps, noRetainedProps, actionless)", signature.asText(getLangProps()));
    }

    @Test
    public void test_parsing_named_params() {
        RPsiLet let = first(letExpressions(parseCode("let padding: v:length -> h:length -> rule")));

        RPsiSignature signature = let.getSignature();
        assertEquals(3, signature.getItems().size());
        assertEquals("v:length -> h:length -> rule", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
        assertEquals("v", signature.getItems().get(0).getName());
        assertFalse(signature.getItems().get(1).isOptional());
        assertEquals("h", signature.getItems().get(1).getName());
        assertEquals("rule", signature.getItems().get(2).getText());
    }

    @Test
    public void test_optional_fun() {
        RPsiLet let = first(letExpressions(parseCode("let x: int -> string option -> string = fun a  -> fun b  -> c")));

        RPsiSignature signature = let.getSignature();
        assertEquals("int -> string option -> string", signature.asText(getLangProps()));

        List<RPsiSignatureItem> items = let.getSignature().getItems();
        assertEquals("int", items.get(0).getText());
        assertFalse(items.get(0).isOptional());
        assertEquals("string option", items.get(1).getText());
        assertFalse(items.get(1).isOptional());
        assertEquals("string", items.get(2).getText());
        assertSize(3, items);
    }

    @Test
    public void test_optional_fun_parameters() {
        RPsiLet let = first(letExpressions(parseCode("let x a b ?(c= false)  ?(d= 1.)  = 3")));

        RPsiFunction function = (RPsiFunction) let.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());

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

    @Test
    public void test_optional_fun_parameters_typed() {
        RPsiLet let = first(letExpressions(parseCode("let x (a : int) (b : string option) ?c:((c : bool)= false)  ?d:((d : float)=1.) = 3")));

        RPsiFunction function = (RPsiFunction) let.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());

        assertSize(4, parameters);
        assertEquals("Dummy.x[a]", parameters.get(0).getQualifiedName());
        assertFalse(parameters.get(0).isOptional());
        assertEquals("Dummy.x[b]", parameters.get(1).getQualifiedName());
        assertFalse(parameters.get(1).isOptional());
        assertEquals("Dummy.x[c]", parameters.get(2).getQualifiedName());
        assertEquals("bool", parameters.get(2).getSignature().asText(getLangProps()));
        assertEquals("false", parameters.get(2).getDefaultValue().getText());
        assertTrue(parameters.get(2).isOptional());
        assertEquals("Dummy.x[d]", parameters.get(3).getQualifiedName());
        assertEquals("float", parameters.get(3).getSignature().asText(getLangProps()));
        assertEquals("1.", parameters.get(3).getDefaultValue().getText());
        assertTrue(parameters.get(3).isOptional());
    }

    @Test
    public void test_unitFunParameter() {
        RPsiLet e = first(letExpressions(parseCode("let x (a : int) () = a")));

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());

        assertSize(2, parameters);
        assertEquals("(a : int)", parameters.get(0).getText());
        assertEquals("()", parameters.get(1).getText());
    }

    @Test
    public void test_signature_items() {
        RPsiLet e = first(letExpressions(parseCode("let createAction: < children : React.element; dispatch : ([ `Arity_1 of Redux.Actions.opaqueFsa ], unit) Js.Internal.fn; url : 'url > Js.t -> React.element;")));
        RPsiSignature signature = e.getSignature();

        assertEquals(2, signature.getItems().size());
    }

    @Test
    public void test_jsObject() {
        RPsiLet e = first(letExpressions(parseCode("let x: < a: string; b: 'a > Js.t -> string")));
        RPsiSignature signature = e.getSignature();

        assertEquals(2, signature.getItems().size());
        RPsiSignatureItem jsObj = signature.getItems().get(0);
        List<RPsiObjectField> fields = new ArrayList<>(PsiTreeUtil.findChildrenOfType(jsObj, RPsiObjectField.class));
        assertSize(2, fields);
        assertEquals(fields.get(0).getName(), "a");
        assertEquals(fields.get(1).getName(), "b");
    }

    @Test
    public void test_option() {
        RPsiVal e = firstOfType(parseCode("val x: string array option"), RPsiVal.class);

        RPsiOption option = PsiTreeUtil.findChildOfType(e, RPsiOption.class);
        assertEquals("string array option", option.getText());
    }

    @Test
    public void test_option_named_params() {
        RPsiExternal e = firstOfType(parseCode("external add : x:int option -> int = \"\""), RPsiExternal.class);

        RPsiOption option = PsiTreeUtil.findChildOfType(e, RPsiOption.class);
        assertEquals("int option", option.getText());
    }
}
