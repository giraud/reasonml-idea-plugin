package com.reason.ide.search.reference;

import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import org.jetbrains.annotations.*;

public class ORFakeResolvedElement extends FakePsiElement {
    private final @NotNull PsiElement mySourceElement;

    public ORFakeResolvedElement(@NotNull PsiElement element) {
        mySourceElement = element;
    }

    @Override
    public @NotNull PsiElement getOriginalElement() {
        return mySourceElement;
    }

    @Override
    public @Nullable PsiElement getParent() {
        return mySourceElement.getParent();
    }

    @Override
    public @Nullable String getText() {
        return mySourceElement.getText();
    }

    @Override
    public @NotNull TextRange getTextRangeInParent() {
        return TextRange.EMPTY_RANGE;
    }
}
