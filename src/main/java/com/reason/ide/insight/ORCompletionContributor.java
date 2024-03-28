package com.reason.ide.insight;

import com.intellij.codeInsight.completion.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.insight.provider.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

abstract class ORCompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {
    static final Log LOG = Log.create("insight");

    ORCompletionContributor(@NotNull ORLangTypes types) {
        extend(CompletionType.BASIC, com.intellij.patterns.PlatformPatterns.psiElement(),
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

                        GlobalSearchScope searchScope = GlobalSearchScope.allScope(position.getProject());

                        if (LOG.isTraceEnabled()) {
                            LOG.debug("»» Completion: position: " + position + ", " + position.getText());
                            LOG.debug("               original: " + originalPosition + ", " + (originalPosition == null ? null : originalPosition.getText()));
                            LOG.debug("                element: " + element);
                            LOG.debug("                 parent: " + parent);
                            LOG.debug("           grand-parent: " + grandParent);
                            LOG.debug("                   file: " + parameters.getOriginalFile());
                            LOG.debug("           search scope: " + searchScope);
                        }

                        // A comment, stop completion
                        if (element instanceof PsiComment) {
                            LOG.debug("comment, stop");
                            return;
                        }

                        // Just after an open/include keyword
                        if (prevNodeType == types.OPEN || prevNodeType == types.INCLUDE) {
                            LOG.debug("the previous keyword is OPEN/INCLUDE");
                            ModuleCompletionProvider.addCompletions(element, searchScope, result);
                            return;
                        }
                        if (parent instanceof RPsiOpen
                                || parent instanceof RPsiInclude
                                || grandParent instanceof RPsiOpen
                                || grandParent instanceof RPsiInclude) {
                            LOG.debug("Inside OPEN/INCLUDE");
                            ModuleCompletionProvider.addCompletions(element, searchScope, result);
                            return;
                        }

                        // Just after a DOT
                        if (prevNodeType == types.DOT) {
                            // But not in a guaranteed uncurried function
                            assert prevLeaf != null;
                            PsiElement prevPrevLeaf = prevLeaf.getPrevSibling();
                            if (prevPrevLeaf != null && prevPrevLeaf.getNode().getElementType() != types.LPAREN) {
                                LOG.debug("the previous element is DOT");

                                if (parent instanceof RPsiTagStart) {
                                    LOG.debug("Inside a Tag start");
                                    JsxNameCompletionProvider.addCompletions(element, types, searchScope, result);
                                    return;
                                }

                                DotExpressionCompletionProvider.addCompletions(element, searchScope, result);
                                return;
                            }
                        }

                        if (prevNodeType == types.SHARPSHARP) {
                            LOG.debug("the previous element is SHARPSHARP");
                            ObjectCompletionProvider.addCompletions(element, result);
                            return;
                        }

                        // Jsx
                        if (element instanceof RPsiUpperTagName) {
                            LOG.debug("Previous element type is TAG_NAME");
                            JsxNameCompletionProvider.addCompletions(element, types, searchScope, result);
                            return;
                        }

                        if (parent instanceof RPsiTagProperty /*inside the prop name*/ || parent instanceof RPsiTagStart || grandParent instanceof RPsiTagStart) {
                            LOG.debug("Inside a Tag start");
                            JsxAttributeCompletionProvider.addCompletions(element, searchScope, result);
                            return;
                        }

                        LOG.debug("Nothing found, free expression");
                        FreeExpressionCompletionProvider.addCompletions(element, prevLeaf, parent, searchScope, result);
                    }
                });
    }
}
