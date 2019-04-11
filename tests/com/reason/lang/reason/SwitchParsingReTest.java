package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("ConstantConditions")
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
        assertSize(1, patterns);
        PsiPatternMatch psiPatternMatch = patterns.iterator().next();
        PsiVariant variant = PsiTreeUtil.findChildOfType(psiPatternMatch, PsiVariant.class);
        assertEquals(RmlTypes.INSTANCE.VARIANT_NAME, variant.getFirstChild().getNode().getElementType());
        assertEquals("Incr", variant.getText());
        assertEquals("counter + 1", psiPatternMatch.getBody().getText());
    }

    public void testPatternMatch() {
        PsiFile psiFile = parseCode("switch (p) { | Typedtree.Partial => \"Partial\" | Total => \"Total\" }");

        PsiSwitch e = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        List<PsiPatternMatch> patterns = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiPatternMatch.class));
        assertSize(2, patterns);

        PsiPatternMatch m1 = patterns.get(0);
        assertEquals("Typedtree", PsiTreeUtil.findChildOfType(m1, PsiUpperSymbol.class).getText());
        assertEquals("\"Partial\"", m1.getBody().getText());

        PsiPatternMatch m2 = patterns.get(1);
        assertEquals("\"Total\"", m2.getBody().getText());
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
