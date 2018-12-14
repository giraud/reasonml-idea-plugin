package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.project.DumbAware;
import com.reason.ide.insight.provider.KeywordCompletionProvider;
import org.jetbrains.annotations.NotNull;

abstract class KeywordCompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor implements DumbAware {

    KeywordCompletionContributor(@NotNull CompletionPatterns patterns) {
        extend(CompletionType.BASIC, patterns.declaration(), new KeywordCompletionProvider("declaration", "module", "open", "in", "include", "type", "let", "external", "Some", "None"));
        extend(CompletionType.BASIC, patterns.keyword(), new KeywordCompletionProvider("keyword", "module", "open", "in", "include", "type", "let", "external", "match", "Some", "None"));
    }

}
