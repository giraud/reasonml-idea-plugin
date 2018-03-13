package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;
import com.reason.lang.MlTypes;
import org.jetbrains.annotations.NotNull;

public class ObjectCompletionProvider extends CompletionProvider<CompletionParameters> {
    public ObjectCompletionProvider(MlTypes types) {

    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        System.out.println("» ObjectCompletionProvider");
    }
}
