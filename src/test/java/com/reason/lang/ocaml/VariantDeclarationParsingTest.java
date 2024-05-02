package com.reason.lang.ocaml;

import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class VariantDeclarationParsingTest extends OclParsingTestCase {
    @Test
    public void test_basic() {
        RPsiType e = first(typeExpressions(parseCode("type t = | Black | White")));

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Black", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals("White", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
    }

    @Test
    public void test_no_pipe_first() {
        RPsiType e = first(typeExpressions(parseCode("type t = V1 | V2")));

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("V1", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals("V2", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
    }

    @Test
    public void test_no_pipe_first_constructor() {
        RPsiType e = first(typeExpressions(parseCode("type t = V1 of string | V2")));

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("V1", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals("V2", declarations.get(1).getVariant().getText());
    }

    @Test
    public void test_constructor() {
        RPsiType e = first(typeExpressions(parseCode("type t = | Hex of string | Rgb of int * int * int")));

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Hex", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals(1, declarations.get(0).getParametersList().size());
        assertEquals("Rgb", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
        assertEquals(3, declarations.get(1).getParametersList().size());
    }

    @Test
    public void test_mixed() {
        RPsiType e = first(typeExpressions(parseCode("type t = | Cannot of reason | Loose | Strict")));

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(3, declarations.size());
        assertEquals("Cannot", declarations.get(0).getVariant().getText());
        assertEquals(1, declarations.get(0).getParametersList().size());
        assertEquals("Loose", declarations.get(1).getVariant().getText());
        assertEquals("Strict", declarations.get(2).getVariant().getText());
    }

    @Test
    public void test_generic() {
        RPsiVariantDeclaration e = firstOfType(parseCode("type cases_pattern_expr_r = | CPatRecord of (qualid * cases_pattern_expr) list"), RPsiVariantDeclaration.class);

        assertEquals("CPatRecord of (qualid * cases_pattern_expr) list", e.getText());
        assertEquals("(qualid * cases_pattern_expr) list", e.getParametersList().get(0).getText());
    }

    @Test // coq:: coqpp/coqpp_ast.mli
    public void test_parens() {
        RPsiVariantDeclaration e = firstOfType(parseCode("type symb = | SymbRules of ((string option * symb) list * code) list"), RPsiVariantDeclaration.class);

        assertNoParserError(e);
        assertEquals("SymbRules of ((string option * symb) list * code) list", e.getText());
        assertEquals("((string option * symb) list * code) list", e.getParametersList().get(0).getText());
    }
}
