package com.reason.ide.insight.provider;

import static com.reason.ide.insight.CompletionUtils.KEYWORD_PRIORITY;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import com.intellij.util.text.CaseInsensitiveStringHashingStrategy;
import jpsplugin.com.reason.Log;
import gnu.trove.THashSet;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

  private static final Log LOG = Log.create("insight.keyword");

  private final String m_debugName;
  private final String[] m_keywords;

  private static final THashSet<String> KEYWORD_WITH_POPUP =
      new THashSet<>(
          Arrays.asList("open", "include"), CaseInsensitiveStringHashingStrategy.INSTANCE);
  private static final InsertHandler<LookupElement> INSERT_SPACE_POPUP =
      new AddSpaceInsertHandler(true);
  private static final InsertHandler<LookupElement> INSERT_SPACE = new AddSpaceInsertHandler(false);

  public KeywordCompletionProvider(String debugName, String... keywords) {
    m_debugName = debugName;
    m_keywords = keywords;
  }

  @Override
  protected void addCompletions(
      @NotNull CompletionParameters parameters,
      @NotNull ProcessingContext context,
      @NotNull CompletionResultSet result) {
    LOG.debug(m_debugName + " expression completion");

    for (String keyword : m_keywords) {
      InsertHandler<LookupElement> insertHandler;
      if ("Some".equals(keyword)) {
        insertHandler = ParenthesesInsertHandler.getInstance(true);
      } else {
        insertHandler = KEYWORD_WITH_POPUP.contains(keyword) ? INSERT_SPACE_POPUP : INSERT_SPACE;
      }

      LookupElementBuilder builder =
          LookupElementBuilder.create(keyword).withInsertHandler(insertHandler).bold();

      result.addElement(PrioritizedLookupElement.withPriority(builder, KEYWORD_PRIORITY));
    }
  }
}
