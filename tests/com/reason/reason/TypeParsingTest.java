package com.reason.reason;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiObjectField;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeBinding;
import com.reason.lang.reason.RmlParserDefinition;

import java.util.Collection;

import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfType;

public class TypeParsingTest extends BaseParsingTestCase {
    public TypeParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testAbstractType() {
        PsiType type = first(parseCode("type t;").getTypeExpressions());
        assertEquals("t", type.getName());
    }

    public void testRecursiveType() {
        PsiType type = first(parseCode("type tree('a) = | Leaf('a) | Tree(tree('a), tree('a));").getTypeExpressions());
        assertEquals("tree('a)", type.getName());
    }

    public void testTypeBindingWithVariant() {
        assertNotNull(first(findChildrenOfType(first(parseCode("type t = | Tick;").getTypeExpressions()), PsiTypeBinding.class)));
    }

    public void testTypeBindingWithRecord() {
        PsiType type = first(parseCode("type t = {count: int,\n [@bs.optional] key: string => unit\n};").getTypeExpressions());

        assertNotNull(first(findChildrenOfType(type, PsiTypeBinding.class)));
        Collection<PsiObjectField> fields = findChildrenOfType(type.getBinding(), PsiObjectField.class);
        assertEquals(2, fields.size());
    }

    public void testTypeSpecialProps() {
        PsiType type = first(parseCode("type props = {\n" +
                "string: string,\n" +
                "ref: Js.nullable(Dom.element) => unit,\n" +
                "method: string};").getTypeExpressions());

        assertNotNull(first(findChildrenOfType(type, PsiTypeBinding.class)));
        Collection<PsiObjectField> fields = findChildrenOfType(type.getBinding(), PsiObjectField.class);
        assertEquals(3, fields.size());
    }


}
