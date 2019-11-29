package com.reason.ide.insight.pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.patterns.InitialPatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;

public class ORPatternCondition extends InitialPatternCondition<PsiElement> {

    ORPatternCondition(@NotNull Class<PsiElement> aAcceptedClass) {
        super(aAcceptedClass);
    }

    @Override
    public boolean accepts(@Nullable Object o, ProcessingContext context) {
        return super.accepts(o, context);
    }

    @Override
    public void append(@NotNull StringBuilder builder, String indent) {
        super.append(builder, indent);
    }
}
