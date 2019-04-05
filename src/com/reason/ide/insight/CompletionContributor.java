package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.reason.Log;
import com.reason.ide.insight.provider.DotExpressionCompletionProvider;
import com.reason.ide.insight.provider.FreeExpressionCompletionProvider;
import com.reason.ide.insight.provider.ModuleCompletionProvider;
import com.reason.ide.insight.provider.ObjectCompletionProvider;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

abstract class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {
    protected static final Log LOG = Log.create("insight");

    CompletionContributor(@NotNull ORTypes types, @NotNull ModulePathFinder modulePathFinder) {
        extend(CompletionType.BASIC, com.intellij.patterns.PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                PsiElement position = parameters.getPosition();
                PsiElement originalPosition = parameters.getOriginalPosition();
                PsiElement element = originalPosition == null ? position : originalPosition;
                /*if (element.getNode().getElementType() == types.SEMI) {
                    // Special case, we use the previous sibling instead (because the dot is not part of the composite element)
                    element = PsiTreeUtil.prevVisibleLeaf(element);
                } else if (element instanceof PsiWhiteSpace && CompletionUtils.getPrevNodeType(element) == types.DOT) {
                    element = PsiTreeUtil.prevVisibleLeaf(element);
                }*/

                PsiElement parent = element == null ? null : element.getParent();
                PsiElement grandParent = parent == null ? null : parent.getParent();

                if (LOG.isTraceEnabled()) {
                    LOG.debug("»» Completion: position: " + position + ", " + position.getText());
                    LOG.debug("               original: " + originalPosition + ", " + (originalPosition == null ? null : originalPosition.getText()));
                    LOG.debug("                element: " + element);
                    LOG.debug("                 parent: " + parent);
                    LOG.debug("           grand-parent: " + grandParent);
                    LOG.debug("                   file: " + parameters.getOriginalFile());
                }

                // Just after an open/include keyword
                IElementType prevNodeType = element == null ? null : CompletionUtils.getPrevNodeType(element);
                if (element != null && (prevNodeType == types.OPEN || prevNodeType == types.INCLUDE)) {
                    LOG.debug("previous keyword is OPEN/INCLUDE");
                    ModuleCompletionProvider.addCompletions(types, element, result);
                    return;
                }
                if (parent instanceof PsiOpen || parent instanceof PsiInclude || grandParent instanceof PsiOpen || grandParent instanceof PsiInclude) {
                    LOG.debug("Inside OPEN/INCLUDE");
                    ModuleCompletionProvider.addCompletions(types, element, result);
                    return;
                }

                // Just after a DOT
                if (element != null && prevNodeType == types.DOT) {
                    LOG.debug("previous element is DOT");
                    DotExpressionCompletionProvider.addCompletions(modulePathFinder, element, result);
                    return;
                }

                if (element != null && prevNodeType == types.SHARPSHARP) {
                    LOG.debug("previous element is SHARPSHARP");
                    ObjectCompletionProvider.addCompletions(element, result);
                    return;
                }

                // Specific completion contributors
                if (element != null && addSpecificCompletions(types, element, parent, grandParent, result)) {
                    return;
                }

                if (element != null) {
                    LOG.debug("Nothing found, free expression");
                    FreeExpressionCompletionProvider.addCompletions(modulePathFinder,  parameters.getOriginalFile().getVirtualFile().getPath(), element, result);
                }
            }
        });
    }

    protected abstract boolean addSpecificCompletions(ORTypes types, PsiElement element, PsiElement parent, PsiElement grandParent, CompletionResultSet result);
}
