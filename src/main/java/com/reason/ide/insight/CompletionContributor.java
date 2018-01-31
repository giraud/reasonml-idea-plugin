package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionType;
import com.reason.ide.insight.provider.FileCompletionProvider;
import com.reason.ide.insight.provider.ModuleCompletionProvider;
import com.reason.lang.MlTypes;
import org.jetbrains.annotations.NotNull;

import static com.reason.ide.insight.CompletionPatterns.declarationPattern;
import static com.reason.ide.insight.CompletionPatterns.openPattern;

abstract class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

    CompletionContributor(@NotNull MlTypes types) {
        extend(CompletionType.BASIC, openPattern(), new ModuleCompletionProvider(types));
        extend(CompletionType.BASIC, declarationPattern(), new FileCompletionProvider());
    }

}
