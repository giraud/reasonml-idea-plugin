package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

public class PolyVariantTest extends RmlParsingTestCase {
    @Test
    public void test_basic_LIdent() {
        RPsiLet e = firstOfType(parseCode("let x = `red;"), RPsiLet.class);
        PsiElement variant = first(ORUtil.findImmediateChildrenOfType(e.getBinding(), myTypes.POLY_VARIANT));

        assertEquals("`red", variant.getText());
    }

    @Test
    public void test_basic_UIdent() {
        RPsiLet e = firstOfType(parseCode("let x = `Red;"), RPsiLet.class);
        PsiElement variant = first(ORUtil.findImmediateChildrenOfType(e.getBinding(), myTypes.POLY_VARIANT));

        assertEquals("`Red", variant.getText());
    }

    @Test
    public void test_pattern_match_constant() {
        PsiFile file = parseCode("let unwrapValue = fun "
                + "  | `String(s) => toJsUnsafe(s) "
                + "  | `bool(b) => toJsUnsafe(Js.Boolean.to_js_boolean(b));");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(1, expressions.size());

        Collection<RPsiPatternMatch> matches = PsiTreeUtil.findChildrenOfType(first(expressions), RPsiPatternMatch.class);
        assertEquals(2, matches.size());
    }
}
