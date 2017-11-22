package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.application.ApplicationManager;
import com.reason.RmlFile;
import com.reason.merlin.MerlinService;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;

public class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

    public CompletionContributor() {
        boolean useMerlin = ApplicationManager.getApplication().getComponent(MerlinService.class).hasVersion();
        if (useMerlin) {
            extend(CompletionType.BASIC, psiElement(), new MerlinCompletionProvider());
        } else {
            extend(CompletionType.BASIC, psiElement().inFile(instanceOf(RmlFile.class)).afterLeaf("."), new ModuleCompletionProvider());
            extend(CompletionType.BASIC, psiElement().inFile(instanceOf(RmlFile.class)), new SimpleCompletionProvider());
        }
    }

}
