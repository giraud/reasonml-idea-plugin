package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import com.reason.ide.files.FileBase;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiModuleName;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;

abstract class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

    CompletionContributor(@NotNull MlTypes types) {
        //MerlinService merlinService = ApplicationManager.getApplication().getComponent(MerlinService.class);
        //boolean useMerlin = merlinService != null && merlinService.hasVersion();
        //if (useMerlin) {
        //    extend(CompletionType.BASIC, psiElement(), new MerlinCompletionProvider());
        //} else {

        extend(CompletionType.BASIC, psiElement().inFile(instanceOf(FileBase.class)), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                PsiElement position = parameters.getPosition();
                PsiFile file = position.getContainingFile();
                PsiElement parent = position.getParent();
                PsiElement grandPa = parent == null ? null : parent.getParent();
                PsiElement originalPosition = parameters.getOriginalPosition();
                PsiElement originalParent = originalPosition == null ? null : originalPosition.getParent();
                PsiElement originalPrevSibling = originalPosition == null ? null : originalPosition.getPrevSibling();
                PsiElement parentPrevSibling = parent.getPrevSibling();

                if (parentPrevSibling.getNode().getElementType() == types.DOT) {
                    PsiElement dotPrevSibling = parentPrevSibling.getPrevSibling();
                    if (dotPrevSibling instanceof PsiModuleName) {
                        // Mod.<|>
                        ModuleDotCompletionProvider.complete(file.getProject(), (PsiModuleName) dotPrevSibling, result);
                    }
                } else if (originalPosition == null || originalParent instanceof FileBase || originalParent instanceof PsiFileModuleImpl) {
                    // We are completing a top level expression, there is no previous expression
                    FileCompletionProvider.complete(file.getProject(), (FileBase) parameters.getOriginalFile(), result);
                } else if (originalPosition instanceof LeafPsiElement) {
                    if (originalPosition.getNode().getElementType() == types.VALUE_NAME) {
                        // Starts a name completion
                        ModuleNameCompletion.complete(file.getProject(), ((FileBase) originalPosition.getContainingFile()).asModule(), originalPosition.getText(), result);
                    }
                }
                //if (grandPa instanceof RmlFile || grandPa instanceof OclFile) {
                //
                //
                //    if (originalPosition == null) {
                //        // Xxx.<IntellijIdeazzz>
                //        // Find the modules before the DOT
                //        PsiElement dotPrevSibling = parentPrevSibling.getPrevSibling();
                //        if (dotPrevSibling instanceof PsiModuleName) {
                //            ModuleDotCompletionProvider.complete(file.getProject(), (PsiModuleName) dotPrevSibling, result);
                //            return;
                //        }
                //    } else {
                //        // Xxx.yy<IntellijIdeazzz>
                //        // Find the modules before the DOT
                //        PsiElement dotPrevSibling = parentPrevSibling.getPrevSibling();
                //        if (dotPrevSibling instanceof PsiModuleName) {
                //            ModuleDotCompletionProvider.complete(file.getProject(), (PsiModuleName) dotPrevSibling, result);
                //            return;
                //        }
                //    }
                //
                //
                //    if (originalPrevSibling != null && originalPrevSibling.getNode().getElementType() == types.DOT) {
                //        // Find the modules before the DOT
                //        PsiElement dotPrevSibling = originalPrevSibling.getPrevSibling();
                //        if (dotPrevSibling instanceof PsiModuleName) {
                //            ModuleDotCompletionProvider.complete(file.getProject(), (PsiModuleName) dotPrevSibling, result);
                //        }
                //    }
                //}
            }
        });
    }

}
