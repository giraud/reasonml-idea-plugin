package com.reason.ide.hints;

import com.intellij.lang.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RmlTypeProvider extends ExpressionTypeProvider<PsiElement> {
    @Override
    public @NotNull String getInformationHint(@NotNull PsiElement element) {
        return "Hint";
    }

    @Override
    public @NotNull String getErrorHint() {
        return "Can't understand target";
    }

    @Override
    public @NotNull List<PsiElement> getExpressionsAt(@NotNull PsiElement elementAt) {
        return Collections.emptyList();
    }
}
