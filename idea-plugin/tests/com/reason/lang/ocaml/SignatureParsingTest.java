package com.reason.lang.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiObjectField;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.signature.ORSignature;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class SignatureParsingTest extends OclParsingTestCase {
  public void test_let() {
    PsiLet e = first(letExpressions(parseCode("let x:int = 1")));

    ORSignature signature = e.getORSignature();
    assertEquals("int", signature.asString(myLanguage));
    assertTrue(signature.isMandatory(0));
  }

  public void test_OCamlBeforeDirective() {
    PsiVal e =
        first(
            valExpressions(
                parseCode(
                    "val bool_of_string_opt : string -> bool option\n(** This is a comment *)\n\n#if BS then\n#end")));

    ORSignature signature = e.getORSignature();
    assertEquals("string -> bool option", signature.asString(myLanguage));
  }

  public void test_val() {
    PsiVal e = first(valExpressions(parseCode("val map : 'a option -> ('a -> 'b) -> 'b option")));

    ORSignature signature = e.getORSignature();
    assertEquals("'a option -> ('a -> 'b) -> 'b option", signature.asString(myLanguage));
    assertFalse(signature.isMandatory(0));
    assertTrue(signature.isMandatory(1));
    assertFalse(signature.isMandatory(2));
  }

  public void test_trimming() {
    PsiLet let =
        first(
            letExpressions(
                parseCode(
                    "let statelessComponent:\n  string ->\n  componentSpec(\n    stateless,\n    stateless,\n    noRetainedProps,\n    noRetainedProps,\n    actionless,\n  );\n")));

    PsiSignature signature = let.getPsiSignature();
    assertEquals(
        "string -> componentSpec(stateless, stateless, noRetainedProps, noRetainedProps, actionless)",
        signature.asString(myLanguage));
  }

  public void test_parsingNamedParams() {
    PsiLet let = first(letExpressions(parseCode("let padding: v:length -> h:length -> rule")));

    ORSignature signature = let.getORSignature();
    assertEquals(3, signature.getTypes().length);
    assertEquals("v:length -> h:length -> rule", signature.asString(myLanguage));
    assertTrue(signature.isMandatory(0));
    assertTrue(signature.isMandatory(1));
  }

  public void test_optionalFun() {
    PsiLet let =
        first(
            letExpressions(
                parseCode("let x: int -> string option -> string = fun a  -> fun b  -> c")));

    ORSignature signature = let.getORSignature();
    assertEquals(3, signature.getTypes().length);
    assertEquals("int -> string option -> string", signature.asString(myLanguage));
    assertTrue(signature.isMandatory(0));
    assertFalse(signature.isMandatory(1));
  }

  public void test_optionalFunParameters() {
    PsiLet let =
        first(
            letExpressions(
                parseCode("let x (a : int) (b : string option) (c : bool) (d : float) = 3")));

    PsiFunction function = (PsiFunction) let.getBinding().getFirstChild();
    List<PsiParameter> parameters = new ArrayList<>(function.getParameters());

    assertSize(4, parameters);
    assertTrue(parameters.get(0).getPsiSignature().asHMSignature().isMandatory(0));
    assertFalse(parameters.get(1).getPsiSignature().asHMSignature().isMandatory(0));
    assertTrue(parameters.get(2).getPsiSignature().asHMSignature().isMandatory(0));
    assertTrue(parameters.get(3).getPsiSignature().asHMSignature().isMandatory(0));
  }

  public void test_unitFunParameter() {
    PsiLet e = first(letExpressions(parseCode("let x (a : int) () = a")));

    PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
    List<PsiParameter> parameters = new ArrayList<>(function.getParameters());

    assertSize(2, parameters);
    assertEquals("(a : int)", parameters.get(0).getText());
    assertEquals("()", parameters.get(1).getText());
  }

  public void test_signatureItems() {
    PsiLet e =
        first(
            letExpressions(
                parseCode(
                    "let createAction: < children : React.element; dispatch : ([ `Arity_1 of Redux.Actions.opaqueFsa ], unit) Js.Internal.fn; url : 'url > Js.t -> React.element;")));
    ORSignature signature = e.getORSignature();

    assertEquals(2, signature.getTypes().length);
  }

  public void test_jsObject() {
    PsiLet e = first(letExpressions(parseCode("let x: < a: string; b: 'a > Js.t -> string")));
    ORSignature signature = e.getORSignature();

    assertEquals(2, signature.getTypes().length);
    PsiSignatureItem jsObj = signature.getItems()[0];
    List<PsiObjectField> fields =
        new ArrayList<>(PsiTreeUtil.findChildrenOfType(jsObj, PsiObjectField.class));
    assertSize(2, fields);
    assertEquals(fields.get(0).getName(), "a");
    assertEquals(fields.get(1).getName(), "b");
  }
}
