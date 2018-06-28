package com.reason.ocaml;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeBinding;
import com.reason.lang.ocaml.OclParserDefinition;

import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfType;

public class TypeParsingTest extends BaseParsingTestCase {
    public TypeParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testAbstractType() {
        PsiType type = first(parseCode("type t").getTypeExpressions());
        assertEquals("t", type.getName());
    }

    public void testRecursiveType() {
        PsiType type = first(parseCode("type 'a tree = | Leaf of 'a | Tree of 'a tree  * 'a tree").getTypeExpressions());
        assertEquals("tree", type.getName());
    }

    public void testTypeBindingWithVariant() {
        assertNotNull(first(findChildrenOfType(first(parseCode("type t = | Tick").getTypeExpressions()), PsiTypeBinding.class)));
    }

    public void testTypeBindingWithRecord() {
        assertNotNull(first(findChildrenOfType(first(parseCode("type t = {count: int;}").getTypeExpressions()), PsiTypeBinding.class)));
    }

}

