package com.reason.lang.ocaml;

import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends OclParsingTestCase {
    @Test
    public void test_let() {
        RPsiLet e = firstOfType(parseCode("let x:int = 1"), RPsiLet.class);

        RPsiSignature signature = e.getSignature();
        assertEquals("int", signature.asText(getLangProps()));
        assertFalse(signature.getItems().get(0).isOptional());
    }

    @Test
    public void test_OCamlBeforeDirective() {
        RPsiVal e = firstOfType(parseCode("val bool_of_string_opt : string -> bool option\n(** This is a comment *)\n\n#if BS then\n#end"), RPsiVal.class);

        RPsiSignature signature = e.getSignature();
        assertEquals("string -> bool option", signature.asText(getLangProps()));
    }

    @Test
    public void test_val() {
        RPsiVal e = firstOfType(parseCode("val map : 'a option -> ('a -> 'b) -> 'b option"), RPsiVal.class);

        RPsiSignature signature = e.getSignature();
        List<RPsiSignatureItem> items = signature.getItems();
        assertEquals("'a option -> ('a -> 'b) -> 'b option", signature.asText(getLangProps()));
        assertFalse(items.get(0).isOptional());
        assertFalse(items.get(1).isOptional());
        assertFalse(items.get(2).isOptional());
    }

    @Test
    public void test_trimming() {
        RPsiLet let = firstOfType(
                parseCode("let statelessComponent:\n  string ->\n  componentSpec(\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  )\n"), RPsiLet.class);

        RPsiSignature signature = let.getSignature();
        assertEquals("string -> componentSpec(stateless, stateless, noRetainedProps, noRetainedProps, actionless)", signature.asText(getLangProps()));
    }

    @Test
    public void test_parsing_named_params() {
        RPsiLet let = firstOfType(parseCode("let padding: v:length -> h:length -> rule"), RPsiLet.class);

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
        RPsiLet let = firstOfType(parseCode("let x: int -> string option -> string = fun a  -> fun b  -> c"), RPsiLet.class);

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
        RPsiLet let = firstOfType(parseCode("let x a b ?(c= false)  ?(d= 1.)  = 3"), RPsiLet.class);

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
        RPsiLet let = firstOfType(parseCode("let x (a : int) (b : string option) ?c:((c : bool)= false) ?d:((d : float)=1.) = 3"), RPsiLet.class);
        //val lcs :  -> t
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
    public void test_named_optional_in_val() {
        RPsiSignature e = firstOfType(parseCode("val diff: ?equal:(elem -> bool) -> t"), RPsiSignature.class);

        assertNoParserError(e);
        assertSize(2, e.getItems());
        assertEquals("?equal:(elem -> bool)", e.getItems().get(0).getText());
        assertEquals("t", e.getItems().get(1).getText());
    }

    @Test
    public void test_unit_fun_parameter() {
        RPsiLet e = firstOfType(parseCode("let x (a : int) () = a"), RPsiLet.class);

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());

        assertSize(2, parameters);
        assertEquals("(a : int)", parameters.get(0).getText());
        assertEquals("()", parameters.get(1).getText());
    }

    @Test
    public void test_signature_items() {
        RPsiLet e = firstOfType(parseCode("let createAction: < children : React.element; dispatch : ([ `Arity_1 of Redux.Actions.opaqueFsa ], unit) Js.Internal.fn; url : 'url > Js.t -> React.element;"), RPsiLet.class);
        RPsiSignature signature = e.getSignature();

        assertEquals(2, signature.getItems().size());
    }

    @Test
    public void test_jsObject() {
        RPsiLet e = firstOfType(parseCode("let x: < a: string; b: 'a > Js.t -> string"), RPsiLet.class);
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
    public void test_option_of_option() {
        List<RPsiSignatureItem> es = childrenOfType(parseCode("val view : 'a t -> ('a option * 'a t) option"), RPsiSignatureItem.class);
        RPsiSignatureItem e0 = es.get(0);
        RPsiSignatureItem e1 = es.get(1);
        RPsiOption e1o = PsiTreeUtil.findChildOfType(e1, RPsiOption.class);
        RPsiOption e1oo = PsiTreeUtil.findChildOfType(e1o, RPsiOption.class);

        assertNoParserError(e0);
        assertNoParserError(e1);
        assertEquals("'a t", e0.getText());
        assertEquals("('a option * 'a t) option", e1.getText());
        assertEquals("('a option * 'a t) option", e1o.getText());
        assertEquals("'a option", e1oo.getText());
    }

    @Test
    public void test_option_named_params() {
        RPsiExternal e = firstOfType(parseCode("external add : x:int option -> int = \"\""), RPsiExternal.class);

        RPsiOption option = PsiTreeUtil.findChildOfType(e, RPsiOption.class);
        assertEquals("int option", option.getText());
    }

    @Test // coq:: clib/diff2.mli
    public void test_functor() {
        RPsiFunctor e = firstOfType(parseCode("module M: functor (I: T) -> (S with type t = I.t)"), RPsiFunctor.class);

        assertNoParserError(e);
        assertEquals("M", e.getName());
        assertEquals("S", e.getReturnType().getText());
        assertNotEmpty(e.getConstraints());
    }

    @Test
    public void test_closed_variant() {
        RPsiLet e = firstOfType(parseCode("let x: [< Css.Types.Length.t | Css.Types.Visibility.t] -> unit = fun _  -> ()"), RPsiLet.class);
        assertNoParserError(e);

        List<IElementType> et = extractUpperSymbolTypes(e);
        assertDoesntContain(et, myTypes.A_VARIANT_NAME, myTypes.UIDENT);
        assertContainsElements(et, myTypes.A_MODULE_NAME);
    }

    @Test
    public void test_open_variant() {
        RPsiLet e = firstOfType(parseCode("let x: [< Css.Types.Length.t | Css.Types.Visibility.t] -> unit = fun _  -> ()"), RPsiLet.class);
        assertNoParserError(e);

        List<IElementType> et = extractUpperSymbolTypes(e);
        assertDoesntContain(et, myTypes.A_VARIANT_NAME, myTypes.UIDENT);
        assertContainsElements(et, myTypes.A_MODULE_NAME);
    }
}
