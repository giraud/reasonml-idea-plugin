package com.reason.lang.core.psi.reference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.FakePsiElement;

public class ORFakeResolvedElement extends FakePsiElement {

    private final PsiElement m_sourceElement;

    public ORFakeResolvedElement(@NotNull PsiElement element) {
        m_sourceElement = element;
    }

    @Nullable
    @Override
    public PsiElement getParent() {
        return m_sourceElement.getContainingFile();
    }

    @Nullable
    @Override
    public String getText() {
        return m_sourceElement.getText();
    }

    @NotNull
    @Override
    public TextRange getTextRangeInParent() {
        return TextRange.EMPTY_RANGE;
    }
}
