package com.reason.ide.insight;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import static com.intellij.patterns.PlatformPatterns.*;

abstract class KeywordCompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor implements DumbAware {
    private static final Log LOG = Log.create("insight.keyword");

    protected static final InsertHandler<LookupElement> INSERT_SPACE = new AddSpaceInsertHandler(false);

    KeywordCompletionContributor(@NotNull ORLangTypes types) {
        extend(CompletionType.BASIC, psiElement(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                PsiElement position = parameters.getPosition();
                PsiElement originalPosition = parameters.getOriginalPosition();
                PsiElement element = originalPosition == null ? position : originalPosition;
                IElementType prevNodeType = CompletionUtils.getPrevNodeType(element);
                PsiElement parent = element.getParent();

                if (LOG.isTraceEnabled()) {
                    LOG.trace("»» Completion: position: " + position + ", " + position.getText());
                    LOG.trace("               original: " + originalPosition + ", " + (originalPosition == null ? null : originalPosition.getText()));
                    LOG.trace("                element: " + element);
                    LOG.trace("                 parent: " + parent);
                    LOG.trace("                   file: " + parameters.getOriginalFile());
                }

                if (originalPosition == null || originalPosition instanceof PsiWhiteSpace) {
                    if (prevNodeType != types.DOT && prevNodeType != types.COMMA && prevNodeType != types.SHARPSHARP
                            && prevNodeType != types.LIDENT && prevNodeType != types.EQ && prevNodeType != types.LT
                            && prevNodeType != types.WITH && prevNodeType != types.LBRACKET) {
                        if (!(parent instanceof RPsiTagStart || parent instanceof RPsiOpen || parent instanceof RPsiInclude)) {
                            addFileKeywords(result);
                        }
                    }
                }
            }
        });
    }

    protected abstract void addFileKeywords(@NotNull CompletionResultSet result);
}
