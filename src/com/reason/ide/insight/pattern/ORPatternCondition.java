package com.reason.ide.insight.pattern;

import com.intellij.patterns.InitialPatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ORPatternCondition extends InitialPatternCondition<PsiElement> {

    public ORPatternCondition(@NotNull Class<PsiElement> aAcceptedClass) {
        super(aAcceptedClass);
    }

    @NotNull
    @Override
    public Class<PsiElement> getAcceptedClass() {
        return super.getAcceptedClass();
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
