package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class PolyVariantParsingTest extends ResParsingTestCase {
    public void test_basic_LIdent() {
        PsiLet e = first(letExpressions(parseCode("let x = #red")));
        PsiElement variant = first(ORUtil.findImmediateChildrenOfType(e.getBinding(), m_types.POLY_VARIANT));

        assertEquals("#red", variant.getText());
    }

    public void test_basic_UIdent() {
        PsiLet e = first(letExpressions(parseCode("let x = #Red")));
        PsiElement variant = first(ORUtil.findImmediateChildrenOfType(e.getBinding(), m_types.POLY_VARIANT));

        assertEquals("#Red", variant.getText());
    }

    public void test_pattern_match_constant() {
        PsiFile file = parseCode("let unwrapValue = x => switch x {"
                + "  | #String(s) => toJsUnsafe(s) "
                + "  | #bool(b) => toJsUnsafe(Js.Boolean.to_js_boolean(b))"
                + "}");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(1, expressions.size());

        Collection<PsiPatternMatch> matches = PsiTreeUtil.findChildrenOfType(first(expressions), PsiPatternMatch.class);
        assertEquals(2, matches.size());
    }

    public void test_open_variant() {
        PsiType e = firstOfType(parseCode("type t = [> #a | Other.t | #b ]"), PsiType.class);

        PsiPolyVariantConstraint c = PsiTreeUtil.findChildOfType(e, PsiPolyVariantConstraint.class);
        assertTrue(c.isOpen());
        assertSize(3, PsiTreeUtil.findChildrenOfType(c, PsiVariantDeclaration.class));
    }

    public void test_closed_variant() {
        PsiType e = firstOfType(parseCode("type t = [< #a | Other.t | #b ]"), PsiType.class);

        PsiPolyVariantConstraint c = PsiTreeUtil.findChildOfType(e, PsiPolyVariantConstraint.class);
        assertFalse(c.isOpen());
        assertSize(3, PsiTreeUtil.findChildrenOfType(c, PsiVariantDeclaration.class));
    }

    public void test_with_path() {
        PsiPolyVariantConstraint e = firstOfType(parseCode("let visibility: [< Css.Types.Length.t | Css.Types.Visibility.t ] => layoutRule"), PsiPolyVariantConstraint.class);

        List<PsiVariantDeclaration> v = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiVariantDeclaration.class));
        assertSize(2, v);
        assertEquals("Css.Types.Length.t", v.get(0).getText());
        assertEquals("Css.Types.Visibility.t", v.get(1).getText());
    }
}
