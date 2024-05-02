package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class PolyVariantParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic_LIdent() {
        RPsiLet e = firstOfType(parseCode("let x = #red"), RPsiLet.class);
        RPsiLowerSymbol ev = firstOfType(e.getBinding(), RPsiLowerSymbol.class);

        assertEquals("#red", ev.getText());
        assertEquals(myTypes.POLY_VARIANT, ev.getNode().getElementType());
    }

    @Test
    public void test_basic_UIdent() {
        RPsiUpperSymbol e = firstOfType(parseCode("let _ = #Red;"), RPsiUpperSymbol.class);

        assertEquals("#Red", e.getText());
        assertEquals(myTypes.POLY_VARIANT, e.getNode().getElementType());
    }

    @Test
    public void test_pattern_match_constant() {
        PsiFile file = parseCode("let unwrapValue = x => switch x {"
                + "  | #String(s) => toJsUnsafe(s) "
                + "  | #bool(b) => toJsUnsafe(Js.Boolean.to_js_boolean(b))"
                + "}");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(1, expressions.size());

        Collection<RPsiPatternMatch> matches = PsiTreeUtil.findChildrenOfType(first(expressions), RPsiPatternMatch.class);
        assertEquals(2, matches.size());
    }

    @Test
    public void test_open_variant() {
        RPsiType e = firstOfType(parseCode("type t = [> #a | Other.t | #b ]"), RPsiType.class);

        RPsiPolyVariantConstraint c = PsiTreeUtil.findChildOfType(e, RPsiPolyVariantConstraint.class);
        assertTrue(c.isOpen());
        assertSize(3, PsiTreeUtil.findChildrenOfType(c, RPsiVariantDeclaration.class));
    }

    @Test
    public void test_closed_variant() {
        RPsiType e = firstOfType(parseCode("type t = [< #a | Other.t | #b ]"), RPsiType.class);

        RPsiPolyVariantConstraint c = PsiTreeUtil.findChildOfType(e, RPsiPolyVariantConstraint.class);
        assertFalse(c.isOpen());
        assertSize(3, PsiTreeUtil.findChildrenOfType(c, RPsiVariantDeclaration.class));
    }

    @Test
    public void test_with_path() {
        RPsiPolyVariantConstraint e = firstOfType(parseCode("let visibility: [< Css.Types.Length.t | Css.Types.Visibility.t ] => layoutRule"), RPsiPolyVariantConstraint.class);

        List<RPsiVariantDeclaration> v = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiVariantDeclaration.class));
        assertSize(2, v);
        assertEquals("Css.Types.Length.t", v.get(0).getText());
        assertEquals("Css.Types.Visibility.t", v.get(1).getText());
    }
}
