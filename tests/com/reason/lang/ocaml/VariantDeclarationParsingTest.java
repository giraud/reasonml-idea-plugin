package com.reason.lang.ocaml;

import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class VariantDeclarationParsingTest extends OclParsingTestCase {
    public void test_basic() {
        PsiType e = first(typeExpressions(parseCode("type t = | Black | White")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Black", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals("White", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
    }

    public void test_no_pipe_first() {
        PsiType e = first(typeExpressions(parseCode("type t = V1 | V2")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("V1", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals("V2", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
    }

    public void test_no_pipe_first_constructor() {
        PsiType e = first(typeExpressions(parseCode("type t = V1 of string | V2")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("V1", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals("V2", declarations.get(1).getVariant().getText());
    }

    public void test_constructor() {
        PsiType e = first(typeExpressions(parseCode("type t = | Hex of string | Rgb of int * int * int")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Hex", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals(1, declarations.get(0).getParameterList().size());
        assertEquals("Rgb", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
        assertEquals(3, declarations.get(1).getParameterList().size());
    }

    public void test_mixed() {
        PsiType e = first(typeExpressions(parseCode("type t = | Cannot of reason | Loose | Strict")));

        List<PsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), PsiVariantDeclaration.class);
        assertEquals(3, declarations.size());
        assertEquals("Cannot", declarations.get(0).getVariant().getText());
        assertEquals(1, declarations.get(0).getParameterList().size());
        assertEquals("Loose", declarations.get(1).getVariant().getText());
        assertEquals("Strict", declarations.get(2).getVariant().getText());
    }

    public void test_generic() {
        PsiVariantDeclaration e = firstOfType(parseCode("type cases_pattern_expr_r = | CPatRecord of (qualid * cases_pattern_expr) list"), PsiVariantDeclaration.class);

        assertEquals("CPatRecord of (qualid * cases_pattern_expr) list", e.getText());
        PsiParameter p = e.getParameterList().iterator().next();
        assertEquals("(qualid * cases_pattern_expr) list", p.getText());
    }
}
