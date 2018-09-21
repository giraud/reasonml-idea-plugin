package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiPatternMatch;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.psi.PsiUpperSymbol;

import java.util.Collection;

public class SwitchParsingReTest extends BaseParsingTestCase {
    public SwitchParsingReTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testPattern() {
        PsiFile psiFile = parseCode("switch (x) { | Some(x) => x; (); | None => () };");

        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        assertNotNull(switch_);

        Collection<PsiPatternMatch> patterns = PsiTreeUtil.findChildrenOfType(switch_, PsiPatternMatch.class);
        assertEquals(2, patterns.size());
    }

    public void testPatternTokenType() {
        PsiFile psiFile = parseCode("switch (action) { | Incr => counter + 1 }");

        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        Collection<PsiPatternMatch> patterns = PsiTreeUtil.findChildrenOfType(switch_, PsiPatternMatch.class);
        PsiPatternMatch psiPatternMatch = patterns.iterator().next();
        PsiUpperSymbol variant = PsiTreeUtil.findChildOfType(psiPatternMatch, PsiUpperSymbol.class);
        assertEquals(RmlTypes.INSTANCE.VARIANT_NAME, variant.getFirstChild().getNode().getElementType());

    }

}
