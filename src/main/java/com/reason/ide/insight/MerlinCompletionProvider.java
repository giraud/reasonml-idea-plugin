package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.reason.icons.Icons;
import com.reason.ide.LineNumbering;
import com.reason.merlin.MerlinService;
import com.reason.merlin.types.MerlinCompletion;
import com.reason.merlin.types.MerlinCompletionEntry;
import com.reason.merlin.types.MerlinPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MerlinCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        MerlinService merlin = ApplicationManager.getApplication().getComponent(MerlinService.class);

        PsiFile originalFile = parameters.getOriginalFile();
        String text = originalFile.getText();
        LineNumbering lineNumbering = new LineNumbering(text);

        String suitablePrefix = findSuitablePrefix(parameters, text);

        MerlinPosition position = lineNumbering.offsetToPosition(parameters.getOffset());
        MerlinCompletion completion = merlin.completions(originalFile.getName(), text, position, suitablePrefix);

        for (MerlinCompletionEntry entry : completion.entries) {
            resultSet.addElement(LookupElementBuilder.
                    create(entry.name).
                    withIcon(getIcon(entry)).
                    withTypeText(entry.desc));
        }
    }

    // find all text on the left of the cursor
    private String findSuitablePrefix(CompletionParameters parameters, String text) {
        int endPos = parameters.getOffset();
        int startPos = endPos;

        // find space or return
        while (startPos > 0) {
            char previousChar = text.charAt(startPos - 1);
            if (previousChar == ' ' || previousChar == '\n' || previousChar == '(') {
                break;
            }
            startPos--;
        }

        return text.substring(startPos, endPos);
    }

    @Nullable
    private Icon getIcon(MerlinCompletionEntry entry) {
        Icon entryIcon = null;
        if ("Type".equals(entry.kind)) {
            entryIcon = Icons.TYPE;
        } else if ("Value".equals(entry.kind)) {
            entryIcon = Icons.VALUE;
        } else if ("Module".equals(entry.kind)) {
            entryIcon = Icons.MODULE;
        } else if ("Signature".equals(entry.kind)) {
            entryIcon = Icons.SIGNATURE;
        }
        return entryIcon;
    }

}
