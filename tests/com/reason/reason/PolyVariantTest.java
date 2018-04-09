package com.reason.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiPatternMatch;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.reason.RmlParserDefinition;

import java.util.Collection;

public class PolyVariantTest extends BaseParsingTestCase {
    public PolyVariantTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testPatternMatchConstant() {
        PsiFileModuleImpl psiFileModule = parseCode("let unwrapValue = fun\n" +
                "  | `String(s) => toJsUnsafe(s)\n" +
                "  | `Bool(b) => toJsUnsafe(Js.Boolean.to_js_boolean(b));\n");

        Collection<PsiNamedElement> expressions = psiFileModule.getExpressions();
        assertEquals(1, expressions.size());

        Collection<PsiPatternMatch> matches = PsiTreeUtil.findChildrenOfType(first(expressions), PsiPatternMatch.class);
        assertEquals(2, matches.size());
    }

}
