package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class VariantDeclarationParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        RPsiType e = first(typeExpressions(parseCode("type color = | Black | White")));

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Black", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals("White", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
    }

    @Test
    public void test_basic_eol() {
        RPsiType e = first(typeExpressions(parseCode("""
                type precision =
                  | YearDay
                  | YearDayMinute
                  | YearDayMinuteSecond
                """)));

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        //assertEquals(3, declarations.size());
        assertEquals("YearDay", declarations.get(0).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(0).getVariant().getNode().getElementType());
        assertEquals("YearDayMinute", declarations.get(1).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(1).getVariant().getNode().getElementType());
        assertEquals("YearDayMinuteSecond", declarations.get(2).getVariant().getText());
        assertEquals(myTypes.A_VARIANT_NAME, declarations.get(2).getVariant().getNode().getElementType());
    }

    @Test
    public void test_basic2() {
        RPsiType e = first(typeExpressions(parseCode("type t = Black | White")));

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("Black", declarations.get(0).getVariant().getText());
        assertEquals("White", declarations.get(1).getVariant().getText());
    }

    @Test
    public void test_constructor() {
        RPsiType e = first(typeExpressions(parseCode("type t = | Hex(string) | Rgb(int, int, int)")));

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
    public void test_constructor2() {
        RPsiType e = first(typeExpressions(parseCode("type t = Hex(string) | Rgb(int, int, int)")));

        List<RPsiVariantDeclaration> declarations = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e.getBinding(), RPsiVariantDeclaration.class));
        assertEquals(2, declarations.size());
        assertEquals("Hex", declarations.get(0).getVariant().getText());
        // assertTrue(declarations.get(0).getVariant().isVariant());
        assertEquals(1, declarations.get(0).getParametersList().size());
        assertEquals("Rgb", declarations.get(1).getVariant().getText());
        assertEquals(3, declarations.get(1).getParametersList().size());
    }

    @Test
    public void test_mixed() {
        RPsiType e = first(typeExpressions(parseCode("type t = | Cannot(reason) | Loose | Strict")));

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(3, declarations.size());
        assertEquals("Cannot", declarations.get(0).getVariant().getText());
        // assertTrue(declarations.get(0).getVariant().isVariant());
        assertEquals(1, declarations.get(0).getParametersList().size());
        assertEquals("Loose", declarations.get(1).getVariant().getText());
        assertEquals("Strict", declarations.get(2).getVariant().getText());
    }

    @Test
    public void test_constructor_inline_record() {
        RPsiType e = firstOfType(parseCode("type t = | X({ a:int, b:int }) | Y({ c:int })"), RPsiType.class);

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(2, declarations.size());
        assertEquals("X", declarations.get(0).getVariant().getText());
        RPsiRecord r0 = PsiTreeUtil.findChildOfType(declarations.get(0), RPsiRecord.class);
        assertSize(2, r0.getFields());
        assertEquals("Y", declarations.get(1).getVariant().getText());
        RPsiRecord r1 = PsiTreeUtil.findChildOfType(declarations.get(1), RPsiRecord.class);
        assertSize(1, r1.getFields());
    }

    @Test
    public void test_as() {
        RPsiType e = firstOfType(parseCode("""
                type t =
                | A
                | @as("B1") B
                | @as("C1") C
                """), RPsiType.class);

        List<RPsiVariantDeclaration> declarations = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiVariantDeclaration.class);
        assertEquals(3, declarations.size());
        assertEquals("A", declarations.get(0).getVariant().getText());
        assertEquals("B", declarations.get(1).getVariant().getText());
        assertEquals("C", declarations.get(2).getVariant().getText());
    }
}
