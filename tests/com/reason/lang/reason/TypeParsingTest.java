package com.reason.lang.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfType;

public class TypeParsingTest extends BaseParsingTestCase {
    public TypeParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testAbstractType() {
        PsiType type = first(typeExpressions(parseCode("type t;")));
        assertEquals("t", type.getName());
    }

    public void testRecursiveType() {
        PsiType type = first(typeExpressions(parseCode("type tree('a) = | Leaf('a) | Tree(tree('a), tree('a));")));
        assertEquals("tree", type.getName());
    }

    public void testTypeBindingWithVariant() {
        assertNotNull(first(findChildrenOfType(first(typeExpressions(parseCode("type t = | Tick;"))), PsiTypeBinding.class)));
    }

    public void testTypeBindingWithRecord() {
        PsiType type = first(typeExpressions(parseCode("type t = {count: int,\n [@bs.optional] key: string => unit\n};")));

        assertNotNull(first(findChildrenOfType(type, PsiTypeBinding.class)));
        Collection<PsiRecordField> fields = findChildrenOfType(type.getBinding(), PsiRecordField.class);
        assertEquals(2, fields.size());
    }

    public void testTypeSpecialProps() {
        PsiType type = first(typeExpressions(parseCode("type props = {\n" +
                "string: string,\n" +
                "ref: Js.nullable(Dom.element) => unit,\n" +
                "method: string};")));

        assertNotNull(first(findChildrenOfType(type, PsiTypeBinding.class)));
        Collection<PsiRecordField> fields = findChildrenOfType(type.getBinding(), PsiRecordField.class);
        assertEquals(3, fields.size());
    }

    public void testTypeBindingWithRecordAs() {
        PsiTypeBinding typeBinding = first(findChildrenOfType(first(typeExpressions(parseCode("type branch_info('branch_type) = { kind: [> | `Master] as 'branch_type, pos: id, };"))), PsiTypeBinding.class));
        PsiRecord record = PsiTreeUtil.findChildOfType(typeBinding, PsiRecord.class);
        List<PsiRecordField> fields = new ArrayList(record.getFields());
        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

}
