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

}

