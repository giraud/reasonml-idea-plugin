package com.reason.ide.insight;

import static com.reason.ide.insight.CompletionUtils.KEYWORD_PRIORITY;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.reason.lang.napkin.ResTypes;
import org.jetbrains.annotations.NotNull;

public class ResKeywordCompletionContributor extends KeywordCompletionContributor {
    private static final String[] KEYWORDS = new String[]{"open", "include", "module", "type", "let", "external", "exception"};

    ResKeywordCompletionContributor() {
        super(ResTypes.INSTANCE);
    }

    @Override
    protected void addFileKeywords(@NotNull CompletionResultSet result) {
        for (String keyword : KEYWORDS) {
            LookupElementBuilder builder = LookupElementBuilder.create(keyword).withInsertHandler(INSERT_SPACE).bold();
            result.addElement(PrioritizedLookupElement.withPriority(builder, KEYWORD_PRIORITY));
        }
    }
}
