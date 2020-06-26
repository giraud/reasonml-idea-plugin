package com.reason.ide.insight.pattern;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.ElementPatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ORElementPattern implements ElementPattern<PsiElement> {
    private final ORElementPatternMatcher m_matcher;

    private ORElementPattern(ORElementPatternMatcher matcher) {
        m_matcher = matcher;
    }

    @NotNull
    public static ORElementPattern create(ORElementPatternMatcher matcher) {
        return new ORElementPattern(matcher);
    }

    @Override
    public boolean accepts(@Nullable Object o) {
        return accepts(o, null);
    }

    @Override
    public boolean accepts(@Nullable Object o, ProcessingContext context) {
        if (o instanceof PsiElement) {
            return m_matcher.accepts((PsiElement) o, context);
        }
        return false;
    }

    @NotNull
    @Override
    public ElementPatternCondition<PsiElement> getCondition() {
        return new ElementPatternCondition<>(new ORPatternCondition(PsiElement.class));
    }
}
