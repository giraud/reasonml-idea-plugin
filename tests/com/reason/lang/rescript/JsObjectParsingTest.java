package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class JsObjectParsingTest extends ResParsingTestCase {
    public void test_basic() {
        PsiLet e = first(letExpressions(parseCode("let x = {\"a\": 1, \"b\": 0}")));

        PsiLetBinding binding = e.getBinding();
        PsiJsObject object = PsiTreeUtil.findChildOfType(binding, PsiJsObject.class);
        assertNotNull(object);

        Collection<PsiObjectField> fields = object.getFields();
        assertEquals(2, fields.size());
    }

    public void test_definition() {
        PsiType e = first(typeExpressions(parseCode("type t = {\n \"a\": UUID.t, \"b\": array<int>\n }")));

        PsiElement binding = e.getBinding();
        PsiJsObject object = PsiTreeUtil.findChildOfType(binding, PsiJsObject.class);
        assertNotNull(object);

        List<PsiObjectField> fields = new ArrayList<>(object.getFields());
        assertEquals(2, fields.size());
        assertEquals("a", fields.get(0).getName());
        assertEquals("UUID.t", fields.get(0).getSignature().getText());
        assertEquals("b", fields.get(1).getName());
        assertEquals("array<int>", fields.get(1).getSignature().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiTagStart.class));
    }

    public void test_in_function() {
        PsiLet e = first(letExpressions(parseCode("let x = fn(~props={\"a\": id, \"b\": 0})")));

        PsiLetBinding binding = e.getBinding();
        PsiJsObject object = PsiTreeUtil.findChildOfType(binding, PsiJsObject.class);

        List<PsiObjectField> fields = new ArrayList<>(object.getFields());
        assertEquals(2, fields.size());
        assertEquals("a", fields.get(0).getName());
        assertEquals("b", fields.get(1).getName());
    }

    public void test_declaring_open() {
        PsiLet e = first(letExpressions(parseCode(
                "let style = {"
                        + "\"marginLeft\": marginLeft, \"marginRight\": marginRight,\"fontSize\": \"inherit\","
                        + "\"fontWeight\": bold ? \"bold\" : \"inherit\","
                        + "\"textTransform\": transform == \"uc\" ? \"uppercase\" : \"unset\",}")));

        PsiLetBinding binding = e.getBinding();
        PsiJsObject object = PsiTreeUtil.findChildOfType(binding, PsiJsObject.class);
        assertNotNull(object);

        Collection<PsiObjectField> fields = object.getFields();
        assertEquals(5, fields.size());
        assertSize(0, PsiTreeUtil.findChildrenOfType(object, PsiSignature.class));
    }

    public void test_module_open() {
        PsiLet e = first(letExpressions(parseCode(
                "let computingProperties = createStructuredSelector({ "
                        + "open ComputingReducers\n"
                        + "{\"lastUpdate\": selectors.getLastUpdate}\n"
                        + "})")));

        PsiLetBinding binding = e.getBinding();
        PsiParameters call = PsiTreeUtil.findChildOfType(binding, PsiParameters.class);
        PsiOpen open = PsiTreeUtil.findChildOfType(call, PsiOpen.class);
        assertEquals("ComputingReducers", open.getPath());
        PsiJsObject jsObject = PsiTreeUtil.findChildOfType(call, PsiJsObject.class);
        assertNotNull(jsObject);
    }

    public void test_deep() {
        PsiLet e = firstOfType(parseCode("let oo = {\"f1\": {\"f11\": 111}, \"f2\": o,\"f3\": {\"f33\": 333} }"), PsiLet.class);

        PsiJsObject o = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), PsiJsObject.class);
        List<PsiObjectField> fields = new ArrayList<>(o.getFields());
        assertSize(3, fields);
        assertInstanceOf(fields.get(0).getValue(), PsiJsObject.class);
    }
}
