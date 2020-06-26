package com.reason.ide.insight;

import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.reason.lang.reason.RmlTypes;

import static com.reason.ide.insight.CompletionUtils.KEYWORD_PRIORITY;

public class RmlKeywordCompletionContributor extends KeywordCompletionContributor {
    private static String[] KEYWORDS = new String[]{"open", "include", "module", "type", "let", "external", "exception"};

    RmlKeywordCompletionContributor() {
        super(RmlTypes.INSTANCE);
    }

    @Override
    protected void addFileKeywords(@NotNull CompletionResultSet result) {
        for (String keyword : KEYWORDS) {
            LookupElementBuilder builder = LookupElementBuilder.create(keyword).
                    withInsertHandler(INSERT_SPACE).
                    bold();

            result.addElement(PrioritizedLookupElement.withPriority(builder, KEYWORD_PRIORITY));
        }
    }
}
