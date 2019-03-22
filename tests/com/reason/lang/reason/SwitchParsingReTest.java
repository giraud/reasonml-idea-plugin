package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

import java.util.Collection;

public class SwitchParsingReTest extends BaseParsingTestCase {
    public SwitchParsingReTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testPattern() {
        FileBase e = parseCode("switch (x) { | Some(x) => x; (); | None => () }");

        assertSize(1, e.getChildren());
        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(e, PsiSwitch.class));
        assertNotNull(switch_);

        Collection<PsiPatternMatch> patterns = PsiTreeUtil.findChildrenOfType(switch_, PsiPatternMatch.class);
        assertEquals(2, patterns.size());
    }

    public void testPatternTokenType() {
        PsiFile psiFile = parseCode("switch (action) { | Incr => counter + 1 }");

        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        Collection<PsiPatternMatch> patterns = PsiTreeUtil.findChildrenOfType(switch_, PsiPatternMatch.class);
        PsiPatternMatch psiPatternMatch = patterns.iterator().next();
        PsiVariantConstructor variant = PsiTreeUtil.findChildOfType(psiPatternMatch, PsiVariantConstructor.class);
        assertEquals(RmlTypes.INSTANCE.VARIANT_NAME, variant.getFirstChild().getNode().getElementType());

    }

    public void testLet() {
        PsiLet e = first(letExpressions(parseCode("let makeId = () => switch (id) { | None => text | Some(i) => i };")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("switch (id) { | None => text | Some(i) => i }", function.getBody().getText());
    }

    public void testReact() {
        PsiFile psiFile = parseCode("switch (reasonStateUpdate) { | NoUpdate => (None, curTotalState) | Update(nextReasonState) => ( None, {\"reasonState\": nextReasonState}, ) }");
        assertSize(1, psiFile.getChildren());
        PsiSwitch e = (PsiSwitch) psiFile.getChildren()[0];

        assertEquals("(reasonStateUpdate)", e.getCondition().getText());
        assertSize(2, e.getPatterns());
    }
}
