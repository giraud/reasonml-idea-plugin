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
import com.reason.RmlFile;
import com.reason.lang.core.psi.PsiModuleFile;
import com.reason.merlin.MerlinService;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;
import static com.reason.lang.RmlTypes.UIDENT;

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
                    //PsiElement grandPa = parent.getParent();
                    PsiElement originalPosition = parameters.getOriginalPosition();
                    //PsiElement originalParent = originalPosition != null ? originalPosition.getParent() : null;

                    if (originalPosition instanceof LeafPsiElement) {
                        if (parent instanceof PsiModuleFile) {
                            if (originalPosition.getNode().getElementType() == UIDENT) {
                                // Starts a ModuleName completion
                                ModuleNameCompletion.addModules(file.getProject(), (PsiModuleFile) parent, originalPosition.getText().toLowerCase(Locale.getDefault()), result);
                            }
                        }
                    }


                }
            });
        }
    }

}
