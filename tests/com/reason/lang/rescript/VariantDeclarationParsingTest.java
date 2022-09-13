package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class VariantDeclarationParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        PsiType e = first(typeExpressions(parseCode("type color = | Black | White")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Black", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals("White", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
    }

    @Test
    public void test_basic2() {
        PsiType e = first(typeExpressions(parseCode("type t = Black | White")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Black", declarations.get(0).getVariant().getText());
        assertEquals("White", declarations.get(1).getVariant().getText());
    }

    @Test
    public void test_constructor() {
        PsiType e = first(typeExpressions(parseCode("type t = | Hex(string) | Rgb(int, int, int)")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Hex", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals(1, declarations.get(0).getParameterList().size());
        assertEquals("Rgb", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
        assertEquals(3, declarations.get(1).getParameterList().size());
    }

    @Test
    public void test_constructor2() {
        PsiType e = first(typeExpressions(parseCode("type t = Hex(string) | Rgb(int, int, int)")));

        List<PsiVariantDeclaration> declarations = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e.getBinding(), PsiVariantDeclaration.class));
        assertEquals(2, declarations.size());
        assertEquals("Hex", declarations.get(0).getVariant().getText());
        // assertTrue(declarations.get(0).getVariant().isVariant());
        assertEquals(1, declarations.get(0).getParameterList().size());
        assertEquals("Rgb", declarations.get(1).getVariant().getText());
        assertEquals(3, declarations.get(1).getParameterList().size());
    }

    @Test
    public void test_mixed() {
        PsiType e = first(typeExpressions(parseCode("type t = | Cannot(reason) | Loose | Strict")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(3, declarations.size());
        assertEquals("Cannot", declarations.get(0).getVariant().getText());
        // assertTrue(declarations.get(0).getVariant().isVariant());
        assertEquals(1, declarations.get(0).getParameterList().size());
        assertEquals("Loose", declarations.get(1).getVariant().getText());
        assertEquals("Strict", declarations.get(2).getVariant().getText());
    }
}
