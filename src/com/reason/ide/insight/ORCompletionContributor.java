package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.reason.Log;
import com.reason.ide.insight.provider.DotExpressionCompletionProvider;
import com.reason.ide.insight.provider.FreeExpressionCompletionProvider;
import com.reason.ide.insight.provider.ModuleCompletionProvider;
import com.reason.ide.insight.provider.ObjectCompletionProvider;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

abstract class ORCompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {
    static final Log LOG = Log.create("insight");

    ORCompletionContributor(@NotNull ORTypes types, @NotNull QNameFinder qnameFinder) {
        extend(
                CompletionType.BASIC,
                com.intellij.patterns.PlatformPatterns.psiElement(),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        PsiElement originalPosition = parameters.getOriginalPosition();
                        PsiElement element = originalPosition == null ? position : originalPosition;
                        PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(element);
                        IElementType prevNodeType = prevLeaf == null ? null : prevLeaf.getNode().getElementType();
                        PsiElement parent = element.getParent();
                        PsiElement grandParent = parent == null ? null : parent.getParent();

                        if (LOG.isTraceEnabled()) {
                            LOG.debug("»» Completion: position: " + position + ", " + position.getText());
                            LOG.debug(
                                    "               original: "
                                            + originalPosition
                                            + ", "
                                            + (originalPosition == null ? null : originalPosition.getText()));
                            LOG.debug("                element: " + element);
                            LOG.debug("                 parent: " + parent);
                            LOG.debug("           grand-parent: " + grandParent);
                            LOG.debug("                   file: " + parameters.getOriginalFile());
                        }

                        // A comment, stop completion
                        if (element instanceof PsiComment) {
                            LOG.debug("comment, stop");
                            return;
                        }

                        // Just after an open/include keyword
                        if (prevNodeType == types.OPEN || prevNodeType == types.INCLUDE) {
                            LOG.debug("the previous keyword is OPEN/INCLUDE");
                            ModuleCompletionProvider.addCompletions(types, element, result);
                            return;
                        }
                        if (parent instanceof PsiOpen
                                || parent instanceof PsiInclude
                                || grandParent instanceof PsiOpen
                                || grandParent instanceof PsiInclude) {
                            LOG.debug("Inside OPEN/INCLUDE");
                            ModuleCompletionProvider.addCompletions(types, element, result);
                            return;
                        }

                        // Just after a DOT
                        if (prevNodeType == types.DOT) {
                            // But not in a guaranteed uncurried function
                            assert prevLeaf != null;
                            PsiElement prevPrevLeaf = prevLeaf.getPrevSibling();
                            if (prevPrevLeaf != null && prevPrevLeaf.getNode().getElementType() != types.LPAREN) {
                                LOG.debug("the previous element is DOT");
                                DotExpressionCompletionProvider.addCompletions(qnameFinder, element, result);
                                return;
                            }
                        }

                        if (prevNodeType == types.SHARPSHARP) {
                            LOG.debug("the previous element is SHARPSHARP");
                            ObjectCompletionProvider.addCompletions(types, element, result);
                            return;
                        }

                        // Specific completion contributors
                        if (addSpecificCompletions(types, element, parent, grandParent, result)) {
                            return;
                        }

                        LOG.debug("Nothing found, free expression");
                        FreeExpressionCompletionProvider.addCompletions(qnameFinder, element, result);
                    }
                });
    }

    protected abstract boolean addSpecificCompletions(ORTypes types, PsiElement element, PsiElement parent, PsiElement grandParent, CompletionResultSet result);
}
