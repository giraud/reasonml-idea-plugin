package com.reason.lang.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeBinding;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfType;

@SuppressWarnings("ConstantConditions")
public class TypeParsingTest extends BaseParsingTestCase {
    public TypeParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testAbstractType() {
        PsiType type = first(typeExpressions(parseCode("type t")));
        assertEquals("t", type.getName());
    }

    public void testRecursiveType() {
        PsiType type = first(typeExpressions(parseCode("type 'a tree = | Leaf of 'a | Tree of 'a tree  * 'a tree")));
        assertEquals("tree", type.getName());
    }

    public void testTypeBindingWithVariant() {
        assertNotNull(first(findChildrenOfType(first(typeExpressions(parseCode("type t = | Tick"))), PsiTypeBinding.class)));
    }

    public void testTypeBindingWithRecord() {
        assertNotNull(first(findChildrenOfType(first(typeExpressions(parseCode("type t = {count: int;}"))), PsiTypeBinding.class)));
    }

    public void testTypeBindingWithRecordAs() {
        PsiTypeBinding typeBinding = first(findChildrenOfType(first(typeExpressions(parseCode("type 'branch_type branch_info = { kind : [> `Master] as 'branch_type; pos : id; }"))), PsiTypeBinding.class));
        PsiRecord record = PsiTreeUtil.findChildOfType(typeBinding, PsiRecord.class);
        List<PsiRecordField> fields = new ArrayList(record.getFields());
        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

}
