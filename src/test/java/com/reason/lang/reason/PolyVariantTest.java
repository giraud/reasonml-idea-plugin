package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

public class PolyVariantTest extends RmlParsingTestCase {
    @Test
    public void test_basic_LIdent() {
        RPsiLet e = firstOfType(parseCode("let x = `red;"), RPsiLet.class);
        RPsiLowerSymbol ev = firstOfType(e.getBinding(), RPsiLowerSymbol.class);

        assertEquals("`red", ev.getText());
        assertEquals(myTypes.POLY_VARIANT, ev.getNode().getElementType());
    }

    @Test
    public void test_basic_UIdent() {
        RPsiUpperSymbol e = firstOfType(parseCode("let _ = `Red;"), RPsiUpperSymbol.class);

        assertEquals("`Red", e.getText());
        assertEquals(myTypes.POLY_VARIANT, e.getNode().getElementType());
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
