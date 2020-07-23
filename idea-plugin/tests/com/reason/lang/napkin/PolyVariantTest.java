package com.reason.lang.napkin;

import java.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiPatternMatch;

@SuppressWarnings("ConstantConditions")
public class PolyVariantTest extends NsParsingTestCase {

    public void test_lident() {
        PsiLet e = first(letExpressions(parseCode("let x = #red")));
        PsiElement variant = ORUtil.findImmediateFirstChildOfType(e.getBinding(), m_types.POLY_VARIANT);

        assertEquals("#red", variant.getText());
    }

    public void test_uident() {
        PsiLet e = first(letExpressions(parseCode("let x = #Red")));
        PsiElement variant = ORUtil.findImmediateFirstChildOfType(e.getBinding(), m_types.POLY_VARIANT);

        assertEquals("#Red", variant.getText());
    }

    public void test_patternMatchConstant() {
        PsiFile file = parseCode("let unwrapValue = switch (x) {" + //
                                         "  | #String(s) => toJsUnsafe(s) " + //
                                         "  | #bool(b) => toJsUnsafe(Js.Boolean.to_js_boolean(b))" + //
                                         "}");
        Collection<PsiNameIdentifierOwner> expressions = expressions(file);

        assertEquals(1, expressions.size());

        Collection<PsiPatternMatch> matches = PsiTreeUtil.findChildrenOfType(first(expressions), PsiPatternMatch.class);
        assertEquals(2, matches.size());
    }
}
