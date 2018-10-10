package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiPatternMatch;

import java.util.Collection;

public class PolyVariantTest extends BaseParsingTestCase {
    public PolyVariantTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testPatternMatchConstant() {
        PsiFile file = parseCode("let unwrapValue = fun " +
                "  | `String(s) => toJsUnsafe(s) " +
                "  | `Bool(b) => toJsUnsafe(Js.Boolean.to_js_boolean(b));");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(1, expressions.size());

        Collection<PsiPatternMatch> matches = PsiTreeUtil.findChildrenOfType(first(expressions), PsiPatternMatch.class);
        assertEquals(2, matches.size());
    }

}
