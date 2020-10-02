package com.reason.ide.insight;

import static com.intellij.patterns.PlatformPatterns.psiElement;

import com.intellij.codeInsight.completion.AddSpaceInsertHandler;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.reason.Log;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

abstract class KeywordCompletionContributor
    extends com.intellij.codeInsight.completion.CompletionContributor implements DumbAware {

  static final Log LOG = Log.create("insight.keyword");
  protected static final InsertHandler<LookupElement> INSERT_SPACE =
      new AddSpaceInsertHandler(false);

  KeywordCompletionContributor(ORTypes types) {
    extend(
        CompletionType.BASIC,
        psiElement(),
        new CompletionProvider<CompletionParameters>() {
          @Override
          protected void addCompletions(
              @NotNull CompletionParameters parameters,
              @NotNull ProcessingContext context,
              @NotNull CompletionResultSet result) {
            PsiElement position = parameters.getPosition();
            PsiElement originalPosition = parameters.getOriginalPosition();
            PsiElement element = originalPosition == null ? position : originalPosition;
            IElementType prevNodeType = CompletionUtils.getPrevNodeType(element);
            PsiElement parent = element.getParent();
            PsiElement grandParent = parent == null ? null : parent.getParent();

            if (LOG.isTraceEnabled()) {
              LOG.trace("»» Completion: position: " + position + ", " + position.getText());
              LOG.trace(
                  "               original: "
                      + originalPosition
                      + ", "
                      + (originalPosition == null ? null : originalPosition.getText()));
              LOG.trace("                element: " + element);
              LOG.trace("                 parent: " + parent);
              LOG.trace("           grand-parent: " + grandParent);
              LOG.trace("                   file: " + parameters.getOriginalFile());
            }

            if (originalPosition == null && parent instanceof FileBase) {
              if (prevNodeType != types.DOT
                  && prevNodeType != types.SHARPSHARP
                  && prevNodeType != types.C_LOWER_SYMBOL) {
                addFileKeywords(result);
              }
            }
          }
        });
  }

  protected abstract void addFileKeywords(@NotNull CompletionResultSet result);
}
