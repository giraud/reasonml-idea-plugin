package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

public class PolyVariantTest extends RmlParsingTestCase {
    public void test_basic_LIdent() {
        PsiLet e = first(letExpressions(parseCode("let x = `red;")));
        PsiElement variant = first(ORUtil.findImmediateChildrenOfType(e.getBinding(), m_types.POLY_VARIANT));

        assertEquals("`red", variant.getText());
    }

    public void test_basic_UIdent() {
        PsiLet e = first(letExpressions(parseCode("let x = `Red;")));
        PsiElement variant = first(ORUtil.findImmediateChildrenOfType(e.getBinding(), m_types.POLY_VARIANT));

        assertEquals("`Red", variant.getText());
    }

    public void test_pattern_match_constant() {
        PsiFile file = parseCode("let unwrapValue = fun "
                + "  | `String(s) => toJsUnsafe(s) "
                + "  | `bool(b) => toJsUnsafe(Js.Boolean.to_js_boolean(b));");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(1, expressions.size());

        Collection<PsiPatternMatch> matches = PsiTreeUtil.findChildrenOfType(first(expressions), PsiPatternMatch.class);
        assertEquals(2, matches.size());
    }
}
