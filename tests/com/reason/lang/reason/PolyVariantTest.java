package com.reason.lang.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiPatternMatch;

import java.util.Collection;

public class PolyVariantTest extends BaseParsingTestCase {
    public PolyVariantTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testPatternMatchConstant() {
        Collection<PsiNamedElement> expressions = expressions(parseCode("let unwrapValue = fun\n" +
                "  | `String(s) => toJsUnsafe(s)\n" +
                "  | `Bool(b) => toJsUnsafe(Js.Boolean.to_js_boolean(b));\n"));

        assertEquals(1, expressions.size());

        Collection<PsiPatternMatch> matches = PsiTreeUtil.findChildrenOfType(first(expressions), PsiPatternMatch.class);
        assertEquals(2, matches.size());
    }

}
