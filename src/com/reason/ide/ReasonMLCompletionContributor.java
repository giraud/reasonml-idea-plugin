package com.reason.ide;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.reason.icons.ReasonMLIcons;
import com.reason.merlin.MerlinService;
import com.reason.merlin.types.MerlinCompletion;
import com.reason.merlin.types.MerlinCompletionEntry;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ReasonMLCompletionContributor extends CompletionContributor {

    public ReasonMLCompletionContributor() {
        MerlinService merlin = ApplicationManager.getApplication().getComponent(MerlinService.class);

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
                LineNumbering lineNumbering = new LineNumbering(parameters.getOriginalFile().getText());

                MerlinCompletion completion = merlin.completions(parameters.getOriginalFile().getName(),
                        /*findSuitablePrefix(parameters)*/"", lineNumbering.offsetToPosition(parameters.getOffset()));
                for (MerlinCompletionEntry entry : completion.entries) {
                    Icon entryIcon = null;
                    if (entry.kind.equals("type")) {
                        entryIcon = ReasonMLIcons.TYPE;
                    }
                    resultSet.addElement(LookupElementBuilder.create(entry.name).withIcon(entryIcon).withTypeText(entry.desc));
                }
            }
        });
    }
}
