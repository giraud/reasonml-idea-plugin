package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import com.reason.ide.files.RmlFile;
import com.reason.merlin.MerlinService;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;
import static com.reason.lang.RmlTypes.DOT;
import static com.reason.lang.RmlTypes.VALUE_NAME;

public class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

    public CompletionContributor() {
        boolean useMerlin = ApplicationManager.getApplication().getComponent(MerlinService.class).hasVersion();
        if (useMerlin) {
            extend(CompletionType.BASIC, psiElement(), new MerlinCompletionProvider());
        } else {
            extend(CompletionType.BASIC, psiElement().inFile(instanceOf(RmlFile.class)), new CompletionProvider<CompletionParameters>() {
                @Override
                protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                    PsiElement position = parameters.getPosition();
                    PsiFile file = position.getContainingFile();
                    PsiElement parent = position.getParent();
                    PsiElement grandPa = parent == null ? null : parent.getParent();
                    PsiElement originalPosition = parameters.getOriginalPosition();
                    PsiElement originalPrevSibling = originalPosition == null ? null : originalPosition.getPrevSibling();
                    //PsiElement originalParent = originalPosition != null ? originalPosition.getParent() : null;

                    if (grandPa instanceof RmlFile) {
                        // We are completing a top level expression
                        if (originalPrevSibling != null && originalPrevSibling.getNode().getElementType() == DOT) {
                            ModuleDotCompletionProvider.complete(result);
                        } else if (originalPosition instanceof LeafPsiElement) {
                            if (originalPosition.getNode().getElementType() == VALUE_NAME) {
                                // Starts a ModuleName completion
                                ModuleNameCompletion.complete(file.getProject(), (RmlFile) grandPa, originalPosition.getText().toLowerCase(Locale.getDefault()), result);
                            }
                        }
                    }
                }
            });
        }
    }

}
