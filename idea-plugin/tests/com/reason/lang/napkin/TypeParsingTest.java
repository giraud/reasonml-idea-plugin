package com.reason.lang.napkin;

import java.util.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiJsObject;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVariantDeclaration;

@SuppressWarnings("ConstantConditions")
public class TypeParsingTest extends NsParsingTestCase {

    public void test_abstract() {
        PsiType e = first(typeExpressions(parseCode("type t")));
        
        assertEquals("t", e.getName());
    }

    public void test_parameterized() {
        PsiType e = first(typeExpressions(parseCode("type declaration_arity<'a, 'b> = | RegularArity('a)")));

        assertEquals("declaration_arity", e.getName());
        assertEquals("| RegularArity('a)", e.getBinding().getText());
    }

    public void test_recursive() {
        PsiType e = first(typeExpressions(parseCode("type rec tree<'a> = | Leaf('a) | Tree(tree<'a>, tree<'a>)")));

        assertEquals("tree", e.getName());
    }

    public void test_variant() {
        PsiType e = first(typeExpressions(parseCode("type t = | Tick")));

        assertNotNull(e.getBinding());
        assertEquals("t", e.getName());
        Collection<PsiVariantDeclaration> vars = PsiTreeUtil.findChildrenOfType(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals("Tick", ORUtil.findImmediateFirstChildOfType(vars.iterator().next(), m_types.C_VARIANT).getText());
    }

    public void test_polyVariant() {
        PsiType e = first(typeExpressions(parseCode("type t = [ #Red | #Blue ]")));
        assertNotNull(e.getBinding());
        assertEquals("t", e.getName());
    }

    public void test_typeBindingWithRecord() {
        PsiType e = first(typeExpressions(parseCode("type t = {count: int,\n @bs.optional key: string => unit\n}")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        Collection<PsiRecordField> fields = record.getFields();
        assertEquals(2, fields.size());
    }

    public void test_typeSpecialProps() {
        PsiType e = first(typeExpressions(parseCode("type props = { string: string,\n ref: Js.nullable<Dom.element> => unit,\n method: string }",true)));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        Collection<PsiRecordField> fields = record.getFields();
        assertEquals(3, fields.size());
    }

    public void test_typeBindingWithRecordAs() {
        PsiType e = first(typeExpressions(parseCode("type branch_info<'branch_type> = { kind: [> | #Master] as 'branch_type, pos: id, }")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        List<PsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

    public void test_scope() {
        PsiExternal e = first(externalExpressions(
                parseCode("external createElement : (reactClass, ~props: Js.t<{..}>=?, array(reactElement)) => reactElement =  \"createElement\"")));

        PsiSignature signature = e.getPsiSignature();
        List<PsiSignatureItem> signatureItems = ORUtil.findImmediateChildrenOfClass(signature, PsiSignatureItem.class);

        assertSize(4, signatureItems);
        assertEquals("reactClass", signatureItems.get(0).getText());
        assertEquals("~props: Js.t<{..}>=?", signatureItems.get(1).getText());
        assertEquals("array(reactElement)", signatureItems.get(2).getText());
        assertEquals("reactElement", signatureItems.get(3).getText());
    }

    public void test_jsObject() {
        PsiType e = first(typeExpressions(parseCode("type t = {. a: string }")));

        assertInstanceOf(e.getBinding().getFirstChild(), PsiJsObject.class);
    }
}
