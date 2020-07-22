package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiPatternMatch;

import java.util.Collection;

public class PolyVariantTest extends RmlParsingTestCase {
    public void test_basicLIdent() {
        PsiLet e = first(letExpressions(parseCode("let x = `red;")));
        PsiElement variant = first(ORUtil.findImmediateChildrenOfType(e.getBinding(), RmlTypes.INSTANCE.POLY_VARIANT));

        assertEquals("`red", variant.getText());
    }

    public void test_basicUIdent() {
        PsiLet e = first(letExpressions(parseCode("let x = `Red;")));
        PsiElement variant = first(ORUtil.findImmediateChildrenOfType(e.getBinding(), RmlTypes.INSTANCE.POLY_VARIANT));

        assertEquals("`Red", variant.getText());
    }

    public void test_patternMatchConstant() {
        PsiFile file = parseCode("let unwrapValue = fun " +
                "  | `String(s) => toJsUnsafe(s) " +
                "  | `bool(b) => toJsUnsafe(Js.Boolean.to_js_boolean(b));");
        Collection<PsiNameIdentifierOwner> expressions = expressions(file);

        assertEquals(1, expressions.size());

        Collection<PsiPatternMatch> matches = PsiTreeUtil.findChildrenOfType(first(expressions), PsiPatternMatch.class);
        assertEquals(2, matches.size());
    }

}
