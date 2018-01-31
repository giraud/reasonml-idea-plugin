package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.project.DumbAware;
import com.reason.ide.insight.provider.KeywordCompletionProvider;

import static com.reason.ide.insight.CompletionPatterns.declarationPattern;

public class KeywordCompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor implements DumbAware {

    public KeywordCompletionContributor() {
        extend(CompletionType.BASIC, declarationPattern(), new KeywordCompletionProvider("module", "open", "include", "type", "let"));
    }

}
