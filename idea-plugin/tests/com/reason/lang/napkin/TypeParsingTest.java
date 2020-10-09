package com.reason.lang.napkin;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class TypeParsingTest extends NsParsingTestCase {

  public void test_abstract() {
    PsiType e = first(typeExpressions(parseCode("type t")));

    assertEquals("t", e.getName());
  }

  public void test_parameterized() {
    PsiType e =
        first(typeExpressions(parseCode("type declaration_arity<'a, 'b> = | RegularArity('a)")));

    assertEquals("declaration_arity", e.getName());
    assertEquals("| RegularArity('a)", e.getBinding().getText());
  }

  public void test_recursive() {
    PsiType e =
        first(
            typeExpressions(
                parseCode("type rec tree<'a> = | Leaf('a) | Tree(tree<'a>, tree<'a>)")));

    assertEquals("tree", e.getName());
    assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiFunctionCallParams.class));
    assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiTag.class));
  }

  public void test_variant() {
    PsiType e = first(typeExpressions(parseCode("type t = | Tick")));

    assertNotNull(e.getBinding());
    assertEquals("t", e.getName());
    Collection<PsiVariantDeclaration> vars =
        PsiTreeUtil.findChildrenOfType(e.getBinding(), PsiVariantDeclaration.class);
    assertEquals("Tick", vars.iterator().next().getText());
  }

  public void test_polyVariant() {
    PsiType e = first(typeExpressions(parseCode("type t = [ #Red | #Blue ]")));
    assertNotNull(e.getBinding());
    assertEquals("t", e.getName());
  }

  public void test_typeBindingWithRecord() {
    PsiType e =
        first(
            typeExpressions(
                parseCode("type t = {count: int,\n @bs.optional key: string => unit\n}")));

    PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
    Collection<PsiRecordField> fields = record.getFields();
    assertEquals(2, fields.size());
  }

  public void test_typeSpecialProps() {
    PsiType e =
        first(
            typeExpressions(
                parseCode(
                    "type props = { string: string,\n ref: Js.nullable<Dom.element> => unit,\n method: string }")));

    PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
    Collection<PsiRecordField> fields = record.getFields();
    assertEquals(3, fields.size());
  }

  public void test_typeBindingWithRecordAs() {
    PsiType e =
        first(
            typeExpressions(
                parseCode(
                    "type branch_info<'branch_type> = { kind: [> | #Master] as 'branch_type, pos: id, }")));

    PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
    List<PsiRecordField> fields = new ArrayList<>(record.getFields());

    assertEquals(2, fields.size());
    assertEquals("kind", fields.get(0).getName());
    assertEquals("pos", fields.get(1).getName());
  }

  public void test_scope() {
    PsiExternal e =
        first(
            externalExpressions(
                parseCode(
                    "external createElement : (reactClass, ~props: Js.t<{..}>=?, array<reactElement>) => reactElement =  \"createElement\"")));

    PsiSignature signature = e.getPsiSignature();
    List<PsiSignatureItem> signatureItems =
        new ArrayList<>(PsiTreeUtil.findChildrenOfType(signature, PsiSignatureItem.class));

    assertSize(5, signatureItems);
    assertEquals("reactClass", signatureItems.get(0).getText());
    assertEquals("~props: Js.t<{..}>=?", signatureItems.get(1).getText());
    assertEquals("Js.t<{..}>", signatureItems.get(2).getText());
    assertEquals("array<reactElement>", signatureItems.get(3).getText());
    assertEquals("reactElement", signatureItems.get(4).getText());
  }

  public void test_jsObject() {
    PsiType e = first(typeExpressions(parseCode("type t = {. a: string }")));

    assertInstanceOf(e.getBinding().getFirstChild(), PsiJsObject.class);
  }

  public void test_applyParams() {
    PsiType e =
        first(
            typeExpressions(
                parseCode("type t<'value> = Belt.Map.t<key, 'value, Comparator.identity>;")));

    assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiFunctionCallParams.class));
    assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiTag.class));
  }
}
