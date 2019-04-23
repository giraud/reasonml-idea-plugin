package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.project.DumbAware;
import com.reason.ide.insight.provider.KeywordCompletionProvider;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;

abstract class KeywordCompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor implements DumbAware {

    KeywordCompletionContributor() {
        extend(CompletionType.BASIC, psiElement().withSuperParent(2, psiFile()), new KeywordCompletionProvider("declaration", "module", "open", "include", "type", "let", "external", "exception"));
    }

}
