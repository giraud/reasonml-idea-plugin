package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVariantConstructor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class VariantDeclarationTest extends BaseParsingTestCase {
    public VariantDeclarationTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiType e = first(typeExpressions(parseCode("type color = | Black | White")));

        List<PsiElement> children = ORUtil.findImmediateChildrenOfType(e.getBinding(), RmlTypes.INSTANCE.C_VARIANT_EXP);
        assertEquals(2, children.size());

        List<PsiVariantConstructor> variants = new ArrayList<>(e.getVariants());
        assertEquals(2, variants.size());
        assertEquals("Black", variants.get(0).getName());
        assertEquals("White", variants.get(1).getName());
    }

    public void testBasic2() {
        PsiType e = first(typeExpressions(parseCode("type color = Black | White", true)));

        List<PsiElement> children = ORUtil.findImmediateChildrenOfType(e.getBinding(), RmlTypes.INSTANCE.C_VARIANT_EXP);
        assertEquals(2, children.size());

        List<PsiVariantConstructor> variants = new ArrayList<>(e.getVariants());
        assertEquals(2, variants.size());
        assertEquals("Black", variants.get(0).getName());
        assertEquals("White", variants.get(1).getName());
    }

    public void testConstructor() {
        PsiType e = first(typeExpressions(parseCode("type color = | Hex(string) | Rgb(int, int, int)")));

        List<PsiElement> children = ORUtil.findImmediateChildrenOfType(e.getBinding(), RmlTypes.INSTANCE.C_VARIANT_EXP);
        assertEquals(2, children.size());

        List<PsiVariantConstructor> variants = new ArrayList<>(e.getVariants());
        assertEquals(2, variants.size());
        assertEquals("Hex", variants.get(0).getName());
        assertEquals(1, variants.get(0).getParameterList().size());
        assertEquals("Rgb", variants.get(1).getName());
        assertEquals(3, variants.get(1).getParameterList().size());
    }

    public void testMixed() {
        PsiType e = first(typeExpressions(parseCode("type unfocusable = | Cannot(reason) | Loose | Strict")));

        List<PsiElement> children = ORUtil.findImmediateChildrenOfType(e.getBinding(), RmlTypes.INSTANCE.C_VARIANT_EXP);
        assertEquals(3, children.size());

        List<PsiVariantConstructor> variants = new ArrayList<>(e.getVariants());
        assertEquals(3, variants.size());
        assertEquals("Cannot", variants.get(0).getName());
        assertEquals(1, variants.get(0).getParameterList().size());
        assertEquals("Loose", variants.get(1).getName());
        assertEquals("Strict", variants.get(2).getName());
    }
}
