package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.ide.insight.provider.ModuleCompletionProvider;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiOpen;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

abstract class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

    CompletionContributor(@NotNull MlTypes types) {
        extend(CompletionType.BASIC, openPattern(), new ModuleCompletionProvider(types));
    }

    private static PsiElementPattern.Capture<PsiElement> openPattern() {
        return psiElement().inside(PsiOpen.class);
    }
}
