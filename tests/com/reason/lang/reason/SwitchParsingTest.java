package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class SwitchParsingTest extends RmlParsingTestCase {
    public void test_pattern_variant() {
        FileBase f = parseCode("switch (x) { | Variant1(x) => x; (); | Variant2 => () }");

        assertEquals(1, childrenCount(f));
        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(f, PsiSwitch.class));
        assertNotNull(switch_);

        PsiBinaryCondition condition =
                ORUtil.findImmediateFirstChildOfClass(switch_, PsiBinaryCondition.class);
        assertEquals("(x)", condition.getText());

        PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(switch_, PsiScopedExpr.class);
        List<PsiPatternMatch> patterns =
                ORUtil.findImmediateChildrenOfClass(scope, PsiPatternMatch.class);
        assertSize(2, patterns);

        assertEmpty(PsiTreeUtil.findChildrenOfType(switch_, PsiVariantDeclaration.class));

        // first pattern
        PsiPatternMatch p1 = patterns.get(0);
        assertEquals("Variant1(x) => x; ();", p1.getText());
        PsiPatternMatchBody p1Body = p1.getBody();
        assertEquals("x; ();", p1Body.getText());
        // assertNotNull(ORUtil.findImmediateFirstChildOfClass(p1Body, PsiUnit.class));

        // second pattern
        PsiPatternMatch p2 = patterns.get(1);
        assertEquals("Variant2 => ()", p2.getText());
        PsiPatternMatchBody p2Body = p2.getBody();
        assertEquals("()", p2Body.getText());
        // assertNotNull(ORUtil.findImmediateFirstChildOfClass(p1Body, PsiUnit.class));
    }

    public void test_pattern_option() {
        FileBase f = parseCode("switch (x) { | Some(x) => x; (); | None => () }");

        assertEquals(1, childrenCount(f));
        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(f, PsiSwitch.class));
        assertNotNull(switch_);

        PsiBinaryCondition condition =
                ORUtil.findImmediateFirstChildOfClass(switch_, PsiBinaryCondition.class);
        assertEquals("(x)", condition.getText());

        PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(switch_, PsiScopedExpr.class);
        List<PsiPatternMatch> patterns =
                ORUtil.findImmediateChildrenOfClass(scope, PsiPatternMatch.class);
        assertSize(2, patterns);

        assertEmpty(PsiTreeUtil.findChildrenOfType(switch_, PsiVariantDeclaration.class));

        // first pattern
        PsiPatternMatch p1 = patterns.get(0);
        PsiPatternMatchBody p1Body = p1.getBody();
        assertEquals("x; ();", p1Body.getText());
        // assertNotNull(ORUtil.findImmediateFirstChildOfClass(p1Body, PsiUnit.class));

        // second pattern
        PsiPatternMatch p2 = patterns.get(1);
        PsiPatternMatchBody p2Body = p2.getBody();
        assertEquals("()", p2Body.getText());
        // assertNotNull(ORUtil.findImmediateFirstChildOfClass(p2Body, PsiUnit.class));
    }

    public void test_pattern_token_type() {
        PsiFile psiFile = parseCode("switch (action) { | Incr => counter + 1 }");

        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        Collection<PsiPatternMatch> patterns =
                PsiTreeUtil.findChildrenOfType(switch_, PsiPatternMatch.class);
        assertSize(1, patterns);
        PsiPatternMatch psiPatternMatch = patterns.iterator().next();
        PsiPatternMatch patternMatch = patterns.iterator().next();
        PsiUpperSymbol variant =
                ORUtil.findImmediateFirstChildOfClass(patternMatch, PsiUpperSymbol.class);
        // assertTrue(variant.isVariant());
        assertEquals("Incr", variant.getText());
        assertEquals("counter + 1", psiPatternMatch.getBody().getText());
    }

    public void test_pattern_match() {
        FileBase f = parseCode("switch (p) { | Typedtree.Partial => \"Partial\" | Total => \"Total\" }");
        PsiSwitch e = first(PsiTreeUtil.findChildrenOfType(f, PsiSwitch.class));

        List<PsiPatternMatch> patterns = new ArrayList<>(ORUtil.findImmediateChildrenOfClass(ORUtil.findImmediateFirstChildOfClass(e, PsiScopedExpr.class), PsiPatternMatch.class));
        assertSize(2, patterns);

        PsiPatternMatch m1 = patterns.get(0);
        assertEquals("Typedtree", PsiTreeUtil.findChildOfType(m1, PsiUpperSymbol.class).getText());
        assertEquals("\"Partial\"", m1.getBody().getText());

        PsiPatternMatch m2 = patterns.get(1);
        assertEquals("\"Total\"", m2.getBody().getText());
    }

    public void test_pattern_match_2() {
        FileBase f = parseCode("let greeting = name => switch (name) { | FirstName(fn) => \"hello \" ++ fn | LastName(ln) => \"hello \" ++ ln };");
        PsiSwitch e = first(PsiTreeUtil.findChildrenOfType(f, PsiSwitch.class));

        List<PsiPatternMatch> patterns =
                new ArrayList<>(
                        ORUtil.findImmediateChildrenOfClass(
                                ORUtil.findImmediateFirstChildOfClass(e, PsiScopedExpr.class),
                                PsiPatternMatch.class));
        assertSize(2, patterns);
    }

    public void test_let() {
        PsiLet e = first(letExpressions(parseCode("let makeId = () => switch (id) { | None => text | Some(i) => i };")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("switch (id) { | None => text | Some(i) => i }", function.getBody().getText());
    }

    public void test_react() {
        FileBase psiFile =
                parseCode("switch (reasonStateUpdate) { | NoUpdate => (None, curTotalState) | Update(nextReasonState) => ( None, {\"reasonState\": nextReasonState}, ) }");

        assertEquals(1, childrenCount(psiFile));
        PsiSwitch e = (PsiSwitch) psiFile.getChildren()[0];

        assertEquals("(reasonStateUpdate)", e.getCondition().getText());
        assertSize(2, e.getPatterns());
    }

    public void test_tuple() {
        PsiSwitch e = firstOfType(
                parseCode("switch (a, b, c) { | (None, Some(x), _) => do(. z) | (_, _, _) => let x = 1; () }"),
                PsiSwitch.class);

        List<PsiPatternMatch> patterns = e.getPatterns();
        assertSize(2, patterns);
        assertEquals("(None, Some(x), _) => do(. z)", patterns.get(0).getText());
        assertEquals("(_, _, _) => let x = 1; ()", patterns.get(1).getText());
    }

    public void test_switch_of_switch() {
        PsiSwitch e = firstOfType(
                parseCode("switch (a) { | None => switch (b) { | X => 1 | Y => 2 } | Some => 3 }"),
                PsiSwitch.class);

        List<PsiPatternMatch> patterns = e.getPatterns();
        assertSize(2, patterns);
        assertEquals("None => switch (b) { | X => 1 | Y => 2 }", patterns.get(0).getText());
        assertEquals("Some => 3", patterns.get(1).getText());
        PsiSwitch inner = PsiTreeUtil.findChildOfType(patterns.get(0), PsiSwitch.class);
        assertSize(2, inner.getPatterns());
    }

    public void test_group() {
        PsiSwitch e = firstOfType(
                parseCode("switch (x) { | V1(y) => Some(y) | V2(_) | Empty | Unknown => None }"),
                PsiSwitch.class);

        List<PsiPatternMatch> patterns = e.getPatterns();
        assertSize(4, patterns);
        assertEquals("V1(y) => Some(y)", patterns.get(0).getText());
        assertEquals("V2(_)", patterns.get(1).getText());
        assertEquals("Empty", patterns.get(2).getText());
        assertEquals("Unknown => None", patterns.get(3).getText());
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/275
    public void test_GH_275() {
        PsiFunction e = firstOfType(
                parseCode("items->Belt.Array.map(i => switch ((i: t)) { | Value => 1 });"),
                PsiFunction.class);

        assertEquals("i => switch ((i: t)) { | Value => 1 }", e.getText());
        PsiSwitch s = (PsiSwitch) e.getBody().getFirstChild();
        assertEquals("((i: t))", s.getCondition().getText());
        assertEquals("Value => 1", s.getPatterns().get(0).getText());
    }

    public void test_GH_275b() {
        PsiSwitch e = firstOfType(
                parseCode("switch (a, b) { | (Some(a'), Some(b')) => let _ = { switch (x) { | None => None }; }; };"),
                PsiSwitch.class);

        assertEquals("switch (a, b) { | (Some(a'), Some(b')) => let _ = { switch (x) { | None => None }; }; }", e.getText());
        assertEquals("let _ = { switch (x) { | None => None }; };", e.getPatterns().get(0).getBody().getText());
    }
}
