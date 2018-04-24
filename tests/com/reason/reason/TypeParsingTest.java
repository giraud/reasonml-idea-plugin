package com.reason.reason;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeBinding;
import com.reason.lang.reason.RmlParserDefinition;

import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfType;

public class TypeParsingTest extends BaseParsingTestCase {
    public TypeParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testAbstractType() {
        PsiType type = first(parseCode("type t;").getTypeExpressions());
        assertEquals("t", type.getName());
    }

    public void testTypeBindingWithVariant() {
        assertNotNull(first(findChildrenOfType(first(parseCode("type t = | Tick;").getTypeExpressions()), PsiTypeBinding.class)));
    }

    public void testTypeBindingWithRecord() {
        assertNotNull(first(findChildrenOfType(first(parseCode("type t = {count: int};").getTypeExpressions()), PsiTypeBinding.class)));
    }
}
