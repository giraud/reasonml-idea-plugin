package com.reason.ide.insight;

import com.reason.lang.MlTypes;
import org.jetbrains.annotations.NotNull;

abstract class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

    CompletionContributor(@NotNull MlTypes types) {
        //MerlinService merlinService = ApplicationManager.getApplication().getComponent(MerlinService.class);
        //boolean useMerlin = merlinService != null && merlinService.hasVersion();
        //if (useMerlin) {
        //    extend(CompletionType.BASIC, psiElement(), new MerlinCompletionProvider());
        //} else {

        //extend(CompletionType.BASIC, psiElement().inside(PsiOpen.class), new CompletionProvider<CompletionParameters>() {
        //    @Override
        //    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        //        System.out.println("»» add completions for open");
        //    }
        //});
        //
        //extend(CompletionType.BASIC, psiElement(), new CompletionProvider<CompletionParameters>() {
        //    @Override
        //    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        //        System.out.println("»» add completion for psiElement");
        //        PsiElement position = parameters.getPosition();
        //        PsiElement prevSibling = position == null ? null : position.getPrevSibling();
        //        PsiElement originalPosition = parameters.getOriginalPosition();
        //        PsiElement originalPrevSibling = originalPosition == null ? null : originalPosition.getPrevSibling();
        //        Project project = position.getContainingFile().getProject();
        //        //PsiElement parent = position.getParent();
        //        //PsiElement grandPa = parent == null ? null : parent.getParent();
        //        //PsiElement originalParent = originalPosition == null ? null : originalPosition.getParent();
        //        //PsiElement parentPrevSibling = parent.getPrevSibling();
        //        //IElementType previousElementType = parentPrevSibling == null ? null : parentPrevSibling.getNode().getElementType();
        //        //IElementType prevSiblingElementType = originalPrevSibling == null ? null : originalPrevSibling.getNode().getElementType();
        //        //PsiElement prev = position.getPrevSibling();
        //        //PsiElement originalPrev = originalPosition.getPrevSibling();
        //        //
        //        //System.out.println("");
        //        //System.out.println("--");
        //        //System.out.println("pos " + position + "(" + position.getText() + ")" + ", original " + originalPosition);
        //        //System.out.println("originalPrev " + originalPrevSibling);
        //        //System.out.println("parent " + parent + ", originalParent " + originalParent);
        //        //System.out.println("prev " + parentPrevSibling + ", originalPrev " + originalPrevSibling);
        //
        //
        //        //PsiElement cursorElement;
        //        //if (originalPosition.getNode().getElementType() == TokenType.WHITE_SPACE && prevSiblingElementType != types.SEMI) {
        //        //    cursorElement = parentPrevSibling;
        //        //} else {
        //        //    cursorElement = originalPosition;
        //        //}
        //        //
        //        //PsiElement expression = PsiTreeUtil.findFirstParent(cursorElement, true, psiElement -> psiElement instanceof PsiOpen);
        //        //System.out.println("");
        //        //System.out.println("expression " + expression);
        //
        //
        //        //
        //        //if (previousElementType == types.DOT) {
        //        //    PsiElement dotPrevSibling = parentPrevSibling.getPrevSibling();
        //        //    if (dotPrevSibling instanceof PsiModuleName) {
        //        //        // Mod.<|>
        //        //        ModuleDotCompletionProvider.complete(file.getProject(), (PsiModuleName) dotPrevSibling, result);
        //        //    }
        //        //}
        //        //else if (prevSiblingElementType == types.OPEN) {
        //        //    System.out.println("open completion");
        //        //}
        //        //else
        //         if (originalPrevSibling == null || originalPrevSibling instanceof PsiFileModuleImpl) {
        //            // We are completing a top level expression, there is no previous expression
        //            FileCompletionProvider.complete(project, (FileBase) parameters.getOriginalFile(), result);
        //        }
        //             // else if (originalPosition instanceof LeafPsiElement) {
        //        //    if (originalPosition.getNode().getElementType() == types.VALUE_NAME) {
        //        //        // Starts a name completion
        //        //        ModuleNameCompletionProvider.complete(file.getProject(), ((FileBase) originalPosition.getContainingFile()).asModule(), originalPosition.getText(), result);
        //        //    }
        //        //}
        //        //
        //
        //
        //        //if (grandPa instanceof RmlFile || grandPa instanceof OclFile) {
        //        //
        //        //
        //        //    if (originalPosition == null) {
        //        //        // Xxx.<IntellijIdeazzz>
        //        //        // Find the modules before the DOT
        //        //        PsiElement dotPrevSibling = parentPrevSibling.getPrevSibling();
        //        //        if (dotPrevSibling instanceof PsiModuleName) {
        //        //            ModuleDotCompletionProvider.complete(file.getProject(), (PsiModuleName) dotPrevSibling, result);
        //        //            return;
        //        //        }
        //        //    } else {
        //        //        // Xxx.yy<IntellijIdeazzz>
        //        //        // Find the modules before the DOT
        //        //        PsiElement dotPrevSibling = parentPrevSibling.getPrevSibling();
        //        //        if (dotPrevSibling instanceof PsiModuleName) {
        //        //            ModuleDotCompletionProvider.complete(file.getProject(), (PsiModuleName) dotPrevSibling, result);
        //        //            return;
        //        //        }
        //        //    }
        //        //
        //        //
        //        //    if (originalPrevSibling != null && originalPrevSibling.getNode().getElementType() == types.DOT) {
        //        //        // Find the modules before the DOT
        //        //        PsiElement dotPrevSibling = originalPrevSibling.getPrevSibling();
        //        //        if (dotPrevSibling instanceof PsiModuleName) {
        //        //            ModuleDotCompletionProvider.complete(file.getProject(), (PsiModuleName) dotPrevSibling, result);
        //        //        }
        //        //    }
        //        //}
        //    }
        //});
    }

}
