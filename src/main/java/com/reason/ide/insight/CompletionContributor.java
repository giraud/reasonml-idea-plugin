package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.reason.ide.insight.provider.FileCompletionProvider;
import com.reason.ide.insight.provider.JsxNameCompletionProvider;
import com.reason.ide.insight.provider.ModuleCompletionProvider;
import com.reason.lang.MlTypes;
import org.jetbrains.annotations.NotNull;

abstract class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

    CompletionContributor(@NotNull MlTypes types, @NotNull CompletionPatterns patterns) {
        // for debug
        //extend(CompletionType.BASIC, com.intellij.patterns.PlatformPatterns.psiElement(), new DebugCompletionProvider());

        extend(CompletionType.BASIC, patterns.open(), new ModuleCompletionProvider(types));
        extend(CompletionType.BASIC, patterns.declaration(), new FileCompletionProvider());
        extend(CompletionType.BASIC, patterns.jsxName(), new JsxNameCompletionProvider());
    }

    static class DebugCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
            PsiElement position = parameters.getPosition();
            PsiElement originalPosition = parameters.getOriginalPosition();
            System.out.println("add completion " + position + " original " + originalPosition);
        }
    }
}
