package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.reason.RmlFile;
import com.reason.merlin.MerlinService;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;
import static com.reason.ide.insight.CompletionConstants.INTELLIJ_IDEA_RULEZZZ;
import static com.reason.lang.RmlTypes.UIDENT;

public class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

    private static final PatternCondition<PsiElement> ModuleNamePattern = new PatternCondition<PsiElement>("ACCEPT") {
        @Override
        public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
            return psiElement.getNode().getElementType() == UIDENT && !INTELLIJ_IDEA_RULEZZZ.equals(psiElement.getText());
        }
    };

    public CompletionContributor() {
        boolean useMerlin = ApplicationManager.getApplication().getComponent(MerlinService.class).hasVersion();
        if (useMerlin) {
            extend(CompletionType.BASIC, psiElement(), new MerlinCompletionProvider());
        } else {
            extend(CompletionType.BASIC, psiElement().inFile(instanceOf(RmlFile.class)).with(ModuleNamePattern), new ModuleNameCompletionProvider());
            //extend(CompletionType.BASIC, psiElement().inFile(instanceOf(RmlFile.class)).afterLeaf("."), new ModuleDotCompletionProvider());
            extend(CompletionType.BASIC, psiElement().inFile(instanceOf(RmlFile.class)), new SimpleCompletionProvider());
        }
    }

}
