package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.ide.insight.provider.KeywordCompletionProvider;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class KeywordCompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor implements DumbAware {
    /*
    "mutable",
    "fun",
    "external",
    "unit",
    "if",
    "else",
    "switch",
    "as",
    "rec",
    "ref",
    "try",
    "raise",
    "for",
    "in",
    "exception",
    "when",
    "and",
    "while",
     */
    public KeywordCompletionContributor() {
        extend(CompletionType.BASIC, declarationPattern(), new KeywordCompletionProvider("module", "open", "include", "type", "let"));
    }

    private static PsiElementPattern.Capture<PsiElement> declarationPattern() {
        return baseDeclarationPattern()/*.and(statementBeginningPattern())*/;
    }

    private static PsiElementPattern.Capture<PsiElement> statementBeginningPattern() {
        return null;
    }

    private static PsiElementPattern.Capture<PsiElement> baseDeclarationPattern() {
        return psiElement().withSuperParent(2, psiElement(PsiFileModuleImpl.class));
    }
}
