package com.reason.lang.napkin;

import static com.reason.lang.core.ExpressionFilterConstants.FILTER_LET;
import static com.reason.lang.core.psi.ExpressionScope.pub;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.PsiFileHelper;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.psi.impl.PsiFunctionBody;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiObjectField;
import com.reason.lang.core.psi.impl.PsiRecord;
import com.reason.lang.core.psi.impl.PsiScopedExpr;
import com.reason.lang.core.psi.impl.PsiTag;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class LetParsingTest extends NsParsingTestCase {

  public void test_constant() {
    PsiLet let = first(letExpressions(parseCode("let x = 1")));

    assertEquals("x", let.getName());
    assertFalse(let.isFunction());
    assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
  }

  public void test_functionBinding() {
    PsiLet let = first(letExpressions(parseCode("let getAttributes = node => { node }")));

    PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
    assertEquals("node => { node }", binding.getText());
    assertTrue(let.isFunction());
    PsiFunction f = let.getFunction();
    assertEquals("node", f.getParameters().iterator().next().getText());
    assertEquals("{ node }", f.getBody().getText());
  }

  public void test_jsObjectBinding() {
    PsiLet let = first(letExpressions(parseCode("let x = {\"u\": \"r\", \"l\": \"lr\"}")));
    assertFalse(let.isFunction());
    assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
  }

  public void test_jsxBinding() {
    PsiLet e = first(letExpressions(parseCode("let make = (~p) => <div/>")));

    assertEquals("make", e.getName());
    assertTrue(e.isFunction());
    PsiFunctionBody body = e.getFunction().getBody();
    assertNotNull(ORUtil.findImmediateFirstChildOfClass(body, PsiTag.class));
  }

  public void test_scopeWithSome() {
    PsiLet let =
        first(
            letExpressions(
                parseCode(
                    "let l = (p) => { switch a { | Some(a) => a () | None => () } Some(z) }")));
    assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
  }

  public void test_scopeWithLIdent() {
    PsiLet let = first(letExpressions(parseCode("let l = (p) => { Js.log(p) returnObj }")));

    PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
    assertNotNull(binding);
  }

  public void test_localScope() {
    PsiLet let = first(letExpressions(parseCode("let x = { let y = 1 y + 3 }")));

    PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
    assertNotNull(binding);
    assertNull(PsiTreeUtil.findChildOfType(binding, PsiRecord.class));
  }

  public void test_record() {
    PsiLet let = first(letExpressions(parseCode("let typeScale = {one: 1.375, two: 1.0}")));

    PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
    assertNotNull(binding);
    PsiRecord record = PsiTreeUtil.findChildOfType(binding, PsiRecord.class);
    assertNotNull(record);
    assertSize(2, record.getFields());
  }

  public void test_signature() {
    PsiLet e =
        first(letExpressions(parseCode("let combine: (style, style) => style = (a, b) => { }")));

    assertEquals("(style, style) => style", e.getORSignature().asString(myLanguage));
    assertEquals("(a, b) => { }", e.getBinding().getText());
    assertTrue(e.isFunction());
  }

  public void test_signature_dot() {
    PsiLet let = first(letExpressions(parseCode("let x: M1.y => M2.z;")));

    assertNull(PsiTreeUtil.findChildOfType(let, PsiFunction.class));
    assertEquals("M1.y => M2.z", let.getPsiSignature().getText());
    List<PsiSignatureItem> items =
        new ArrayList<>(
            PsiTreeUtil.findChildrenOfType(let.getPsiSignature(), PsiSignatureItem.class));
    assertEquals("M1.y", items.get(0).getText());
  }

  public void test_signatureJsObject() {
    PsiLet let = first(letExpressions(parseCode("let x: {. a:string, b:int } => unit;")));

    assertEquals("{. a:string, b:int } => unit", let.getPsiSignature().getText());
    List<PsiObjectField> fields =
        new ArrayList<>(PsiTreeUtil.findChildrenOfType(let, PsiObjectField.class));
    assertEquals("a:string", fields.get(0).getText());
    assertEquals("b:int", fields.get(1).getText());
  }

  public void test_rec() {
    PsiLet let = first(letExpressions(parseCode("let rec lx = x => x + 1")));

    assertEquals("lx", let.getName());
    assertTrue(let.isFunction());
  }

  public void test_signatureB() {
    FileBase file = parseCode("let watchUrl: (url => unit) => watcherID");
    PsiLet e = first(letExpressions(file));

    assertTrue(e.isFunction());
    assertEquals("watchUrl", e.getName());
    assertEquals("(url => unit) => watcherID", e.getPsiSignature().getText());
  }

  public void test_letAndInModule() {
    FileBase file = parseCode("module M = { let f1 = x => x and f2 = y => y }");
    Collection<PsiNamedElement> es =
        PsiFileHelper.getModuleExpressions(file).iterator().next().getExpressions(pub, FILTER_LET);

    assertSize(2, es);
    assertEquals("f2 = y => y", second(es).getText());
  }

  public void test_alias() {
    PsiLet e = first(letExpressions(parseCode("let x = M1.M2.y")));

    assertEquals("x", e.getName());
    assertEquals("M1.M2.y", e.getAlias());
  }

  public void test_deconstruction() {
    PsiLet e = first(letExpressions(parseCode("let (a, b) = x")));

    assertTrue(e.isDeconsruction());
    List<PsiElement> names = e.getDeconstructedElements();
    assertSize(2, names);
    assertEquals("a", names.get(0).getText());
    assertInstanceOf(names.get(0), PsiLowerIdentifier.class);
    assertEquals("b", names.get(1).getText());
    assertInstanceOf(names.get(1), PsiLowerIdentifier.class);
    assertSize(2, PsiTreeUtil.findChildrenOfType(e, PsiLowerIdentifier.class));
  }

  public void test_function_record() {
    List<PsiLet> es = letExpressions(parseCode("let x = y(M.{i: string}); let z=2;"));

    assertSize(2, es);
    assertNull(PsiTreeUtil.findChildOfType(es.get(0), PsiScopedExpr.class));
    assertEquals("i", PsiTreeUtil.findChildOfType(es.get(0), PsiRecordField.class).getName());
    assertEquals("{i: string}", PsiTreeUtil.findChildOfType(es.get(0), PsiRecord.class).getText());
  }

  public void test_braces() {
    PsiLet e = first(letExpressions(parseCode("let x = p => { test ? { call(Some(a)) } : b }")));

    assertEquals("x", e.getName());
    assertTrue(e.isFunction());
    PsiFunction f = e.getFunction();
    assertEquals("{ test ? { call(Some(a)) } : b }", f.getBody().getText());
  }

  // public void test_customOperator() {
  // let \"/" = ...
  // PsiLet e = first(letExpressions(parseCode("let \\\"try\" = true")));
  //
  // assertEquals("try", e.getName());
  // }

  // zzz DEPRECATED ?
  // public void testIssue105() {
  //    FileBase file = parseCode("let string = \"x\"");
  //    PsiLet e = first(letExpressions(file));
  //
  //    assertFalse(e.isFunction());
  //    assertEquals("string", e.getName());
  // }
  //
  // public void testIssue105a() {
  //    FileBase file = parseCode("let int = 1");
  //    PsiLet e = first(letExpressions(file));
  //
  //    assertFalse(e.isFunction());
  //    assertEquals("int", e.getName());
  // }
  //
  // public void testIssue105b() {
  //    FileBase file = parseCode("let bool = 1");
  //    PsiLet e = first(letExpressions(file));
  //
  //    assertFalse(e.isFunction());
  //    assertEquals("bool", e.getName());
  // }
  //
  // public void test_private() {
  //    PsiLet e = first(letExpressions(parseCode("let x%private = 1")));
  //
  //    assertEquals("x", e.getName());
  //    assertTrue(e.isPrivate());
  // }
}
