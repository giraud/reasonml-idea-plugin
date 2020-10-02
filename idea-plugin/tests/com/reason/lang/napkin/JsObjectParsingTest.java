package com.reason.lang.napkin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.psi.PsiJsObject;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiLocalOpen;
import com.reason.lang.core.psi.PsiObjectField;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.psi.PsiType;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class JsObjectParsingTest extends NsParsingTestCase {
  public void test_basic() {
    PsiLet e = first(letExpressions(parseCode("let x = {\"a\": 1, \"b\": 0};")));

    PsiLetBinding binding = e.getBinding();
    PsiJsObject object = PsiTreeUtil.findChildOfType(binding, PsiJsObject.class);
    assertNotNull(object);

    Collection<PsiObjectField> fields = object.getFields();
    assertEquals(2, fields.size());
  }

  public void test_definition() {
    PsiType e = first(typeExpressions(parseCode("type t = {. \"a\": UUID.t, \"b\": int};")));

    PsiElement binding = e.getBinding();
    PsiJsObject object = PsiTreeUtil.findChildOfType(binding, PsiJsObject.class);
    assertNotNull(object);

    List<PsiObjectField> fields = new ArrayList<>(object.getFields());
    assertEquals(2, fields.size());
    assertEquals("a", fields.get(0).getName());
    assertEquals("UUID.t", fields.get(0).getSignature().getText());
    assertEquals("b", fields.get(1).getName());
    assertEquals("int", fields.get(1).getSignature().getText());
  }

  public void test_inFunction() {
    PsiLet e = first(letExpressions(parseCode("let x = fn(~props={\"a\": id, \"b\": 0})")));

    PsiLetBinding binding = e.getBinding();
    PsiJsObject object = PsiTreeUtil.findChildOfType(binding, PsiJsObject.class);
    assertNotNull(object);

    Collection<PsiObjectField> fields = object.getFields();
    assertEquals(2, fields.size());
  }

  public void test_declaringOpen() {
    PsiLet e =
        first(
            letExpressions(
                parseCode(
                    "let style = {"
                        + //
                        "\"marginLeft\": marginLeft, \"marginRight\": marginRight,\"fontSize\": \"inherit\","
                        + "\"fontWeight\": bold ? \"bold\" : \"inherit\","
                        + "\"textTransform\": transform == \"uc\" ? \"uppercase\" : \"unset\",}")));

    PsiLetBinding binding = e.getBinding();
    PsiJsObject object = PsiTreeUtil.findChildOfType(binding, PsiJsObject.class);
    assertNotNull(object);

    Collection<PsiObjectField> fields = object.getFields();
    assertEquals(5, fields.size());
    assertSize(0, PsiTreeUtil.findChildrenOfType(object, PsiSignature.class));
  }

  public void test_moduleOpen() {
    PsiLet e =
        first(
            letExpressions(
                parseCode(
                    "let computingProperties = createStructuredSelector("
                        + //
                        "    ComputingReducers.{ \"lastUpdate\": selectors.getLastUpdate },\n"
                        + //
                        "  );")));

    PsiLetBinding binding = e.getBinding();
    PsiFunctionCallParams call = PsiTreeUtil.findChildOfType(binding, PsiFunctionCallParams.class);
    PsiLocalOpen open = PsiTreeUtil.findChildOfType(call, PsiLocalOpen.class);
    PsiJsObject jsObject = ORUtil.findImmediateFirstChildOfClass(open, PsiJsObject.class);
    assertNotNull(jsObject);
  }
}
