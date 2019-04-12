package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVariantDeclaration;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class VariantDeclarationTest extends BaseParsingTestCase {
    public VariantDeclarationTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testBasic() {
        PsiType e = first(typeExpressions(parseCode("type color = | Black | White")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Black", declarations.get(0).getVariant().getName());
        assertTrue(declarations.get(0).getVariant().isVariant());
        assertEquals("White", declarations.get(1).getVariant().getName());
        assertTrue(declarations.get(1).getVariant().isVariant());
    }

    public void testBasic2() {
        PsiType e = first(typeExpressions(parseCode("type color = Black | White")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Black", declarations.get(0).getVariant().getName());
        assertTrue(declarations.get(0).getVariant().isVariant());
        assertEquals("White", declarations.get(1).getVariant().getName());
        assertTrue(declarations.get(1).getVariant().isVariant());
    }

    public void testConstructor() {
        PsiType e = first(typeExpressions(parseCode("type color = | Hex of string | Rgb of int * int * int")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Hex", declarations.get(0).getVariant().getName());
        assertTrue(declarations.get(0).getVariant().isVariant());
        assertEquals(1, declarations.get(0).getParameterList().size());
        assertEquals("Rgb", declarations.get(1).getVariant().getName());
        assertTrue(declarations.get(1).getVariant().isVariant());
        assertEquals(3, declarations.get(1).getParameterList().size());
    }

    public void testMixed() {
        PsiType e = first(typeExpressions(parseCode("type unfocusable = | Cannot of reason | Loose | Strict")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(3, declarations.size());
        assertEquals("Cannot", declarations.get(0).getVariant().getName());
        assertTrue(declarations.get(0).getVariant().isVariant());
        assertEquals(1, declarations.get(0).getParameterList().size());
        assertEquals("Loose", declarations.get(1).getVariant().getName());
        assertEquals("Strict", declarations.get(2).getVariant().getName());
    }
}
