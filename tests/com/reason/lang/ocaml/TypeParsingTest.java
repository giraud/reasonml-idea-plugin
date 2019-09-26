package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

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
        PsiFile file = parseCode("type t = {count: int;}");

        assertNotNull(first(findChildrenOfType(first(typeExpressions(file)), PsiTypeBinding.class)));
    }

    @SuppressWarnings("unchecked")
    public void testTypeBindingWithRecordAs() {
        PsiTypeBinding typeBinding = first(findChildrenOfType(first(typeExpressions(parseCode("type 'branch_type branch_info = { kind : [> `Master] as 'branch_type; pos : id; }"))), PsiTypeBinding.class));
        PsiRecord record = PsiTreeUtil.findChildOfType(typeBinding, PsiRecord.class);
        List<PsiRecordField> fields = new ArrayList(record.getFields());
        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

    public void testChainDef() {
        FileBase file = parseCode("type 'branch_type branch_info = 'branch_type Vcs_.branch_info = { kind : [> `Master] as 'branch_type; root : id; pos  : id; }");

        assertEquals(1, file.getChildren().length);
    }

    public void testParameterizedType() {
        PsiType e = first(typeExpressions(parseCode("type ('a, 'b) declaration_arity = | RegularArity of 'a")));

        assertEquals("declaration_arity", e.getName());
        assertEquals("| RegularArity of 'a", e.getBinding().getText());

        PsiTypeConstrName cname = e.getConstrName();
        assertTrue(cname.hasParameters());
    }

}
