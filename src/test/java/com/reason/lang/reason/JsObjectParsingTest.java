package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class JsObjectParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        RPsiLet e = firstOfType(parseCode("let x = {\"a\": 1, \"b\": 0};"), RPsiLet.class);

        RPsiLetBinding binding = e.getBinding();
        RPsiJsObject object = PsiTreeUtil.findChildOfType(binding, RPsiJsObject.class);
        assertNotNull(object);

        Collection<RPsiObjectField> fields = object.getFields();
        assertEquals(2, fields.size());
    }

    @Test
    public void test_definition() {
        RPsiType e = firstOfType(parseCode("""
                type t = {.
                  "a": UUID.t, "b": { "c": array(int) }
                };
                """), RPsiType.class);

        PsiElement binding = e.getBinding();
        RPsiJsObject object = PsiTreeUtil.findChildOfType(binding, RPsiJsObject.class);
        assertNotNull(object);

        List<RPsiObjectField> fields = new ArrayList<>(object.getFields());
        assertSize(2, fields);
        assertEquals("a", fields.getFirst().getName());
        assertEquals("UUID.t", fields.getFirst().getSignature().getText());
        assertEquals("b", fields.get(1).getName());
        assertEquals("{ \"c\": array(int) }", fields.get(1).getSignature().getText());
        RPsiSignatureItem si0 = fields.get(1).getSignature().getItems().getFirst();
        List<RPsiObjectField> si0fs = ((RPsiJsObject) si0.getFirstChild()).getFields();
        assertEquals("c", si0fs.getFirst().getName());
        assertEquals("array(int)", si0fs.getFirst().getSignature().getText());
    }

    @Test
    public void test_in_function() {
        RPsiLet e = firstOfType(parseCode("let x = fn(~props={\"a\": id, \"b\": 0});"), RPsiLet.class);

        RPsiLetBinding binding = e.getBinding();
        RPsiJsObject object = PsiTreeUtil.findChildOfType(binding, RPsiJsObject.class);

        List<RPsiObjectField> fields = new ArrayList<>(object.getFields());
        assertEquals(2, fields.size());
        assertEquals("a", fields.get(0).getName());
        assertEquals("b", fields.get(1).getName());
    }

    @Test
    public void test_declaring_open() {
        RPsiLet e = firstOfType(parseCode(
                "let style = {"
                        + "\"marginLeft\": marginLeft, "
                        + "\"marginRight\": marginRight,"
                        + "\"fontSize\": \"inherit\","
                        + "\"fontWeight\": bold ? \"bold\" : \"inherit\","
                        + "\"textTransform\": transform == \"uc\" ? \"uppercase\" : \"unset\",};"), RPsiLet.class);

        RPsiLetBinding binding = e.getBinding();
        RPsiJsObject object = PsiTreeUtil.findChildOfType(binding, RPsiJsObject.class);
        assertNotNull(object);

        Collection<RPsiObjectField> fields = object.getFields();
        assertEquals(5, fields.size());
        assertSize(0, PsiTreeUtil.findChildrenOfType(object, RPsiSignature.class));
    }

    @Test
    public void test_module_open() {
        RPsiLet e = firstOfType(parseCode(
                "let computingProperties = createStructuredSelector("
                        + "    ComputingReducers.{ \"lastUpdate\": selectors.getLastUpdate },\n"
                        + "  );"), RPsiLet.class);

        RPsiLetBinding binding = e.getBinding();
        RPsiParameters call = PsiTreeUtil.findChildOfType(binding, RPsiParameters.class);
        RPsiLocalOpen open = PsiTreeUtil.findChildOfType(call, RPsiLocalOpen.class);
        RPsiJsObject jsObject = ORUtil.findImmediateFirstChildOfClass(open, RPsiJsObject.class);
        assertNotNull(jsObject);
    }

    @Test
    public void test_deep() {
        RPsiLet e = firstOfType(parseCode("let oo = {\"f1\": {\"f11\": 111}, \"f2\": o,\"f3\": {\"f33\": 333} }"), RPsiLet.class);

        RPsiJsObject o = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), RPsiJsObject.class);
        List<RPsiObjectField> fields = new ArrayList<>(o.getFields());
        assertSize(3, fields);
        assertInstanceOf(fields.get(0).getValue().getFirstChild(), RPsiJsObject.class);
    }
}
