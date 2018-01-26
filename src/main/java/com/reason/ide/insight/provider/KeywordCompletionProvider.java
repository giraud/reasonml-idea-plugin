package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.reason.ide.insight.CompletionConstants.KEYWORD_PRIORITY;

public class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final String[] m_keywords;

    public KeywordCompletionProvider(String... keywords) {
        m_keywords = keywords;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        for (String keyword : m_keywords) {
            LookupElementBuilder builder = LookupElementBuilder.create(keyword).
                    withInsertHandler(null).
                    bold();

            result.addElement(PrioritizedLookupElement.withPriority(builder, KEYWORD_PRIORITY));
        }
    }
}
