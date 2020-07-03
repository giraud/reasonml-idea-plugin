package com.reason.lang.napkin;

import java.util.*;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiJsObject;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.psi.PsiType;

@SuppressWarnings("ConstantConditions")
public class TypeParsingTest extends BaseParsingTestCase {
    public TypeParsingTest() {
        super("", "res", new NsParserDefinition());
    }

    public void testAbstractType() {
        PsiType e = first(typeExpressions(parseCode("type t;")));
        assertEquals("t", e.getName());
    }

    public void testRecursiveType() {
        PsiType e = first(typeExpressions(parseCode("type tree('a) = | Leaf('a) | Tree(tree('a), tree('a));")));
        assertEquals("tree", e.getName());
    }

    public void testTypeBindingWithVariant() {
        PsiType e = first(typeExpressions(parseCode("type t = | Tick;")));
        assertNotNull(e.getBinding());
    }

    public void testTypeBindingWithRecord() {
        PsiType e = first(typeExpressions(parseCode("type t = {count: int,\n [@bs.optional] key: string => unit\n};")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        Collection<PsiRecordField> fields = record.getFields();
        assertEquals(2, fields.size());
    }

    public void testTypeSpecialProps() {
        PsiType e = first(typeExpressions(parseCode("type props = { " +
                "string: string, " +
                "ref: Js.nullable(Dom.element) => unit, " +
                "method: string };")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        Collection<PsiRecordField> fields = record.getFields();
        assertEquals(3, fields.size());
    }

    public void testTypeBindingWithRecordAs() {
        PsiType e = first(typeExpressions(parseCode("type branch_info('branch_type) = { kind: [> | `Master] as 'branch_type, pos: id, };")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        List<PsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

    public void testTypeParameterized() {
        PsiType e = first(typeExpressions(parseCode("type declaration_arity('a, 'b) = | RegularArity('a);")));
        assertEquals("declaration_arity", e.getName());
        assertEquals("| RegularArity('a)", e.getBinding().getText());
    }

    public void testScope() {
        PsiExternal e = first(externalExpressions(parseCode("external createElement : (reactClass, ~props: Js.t({..})=?, array(reactElement)) => reactElement =  \"createElement\"")));

        PsiSignature signature = e.getPsiSignature();
        List<PsiSignatureItem> signatureItems = ORUtil.findImmediateChildrenOfClass(signature, PsiSignatureItem.class);

        assertSize(4, signatureItems);
        assertEquals("reactClass", signatureItems.get(0).getText());
        assertEquals("~props: Js.t({..})=?", signatureItems.get(1).getText());
        assertEquals("array(reactElement)", signatureItems.get(2).getText());
        assertEquals("reactElement", signatureItems.get(3).getText());
    }

    public void testJsObject() {
        PsiType e = first(typeExpressions(parseCode("type t = {. a: string };")));

        assertInstanceOf(e.getBinding().getFirstChild(), PsiJsObject.class);
    }
}
