package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.CaseInsensitiveStringHashingStrategy;
import com.reason.Log;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import static com.reason.ide.insight.CompletionConstants.KEYWORD_PRIORITY;

public class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

    @NotNull
    private final Log m_debug;
    private final String m_debugName;
    private final String[] m_keywords;

    private static final THashSet<String> KEYWORD_WITH_POPUP = ContainerUtil.newTroveSet(CaseInsensitiveStringHashingStrategy.INSTANCE, "open", "include");
    private static final AddSpaceInsertHandler INSERT_SPACE_POPUP = new AddSpaceInsertHandler(true);
    private static final AddSpaceInsertHandler INSERT_SPACE = new AddSpaceInsertHandler(false);

    public KeywordCompletionProvider(String debugName, String... keywords) {
        m_debug = new Log(Logger.getInstance("ReasonML.insight.keyword"));
        m_debugName = debugName;
        m_keywords = keywords;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        m_debug.debug(m_debugName + " expression completion");

        for (String keyword : m_keywords) {
            LookupElementBuilder builder = LookupElementBuilder.create(keyword).
                    withInsertHandler(KEYWORD_WITH_POPUP.contains(keyword) ? INSERT_SPACE_POPUP : INSERT_SPACE).
                    bold();

            result.addElement(PrioritizedLookupElement.withPriority(builder, KEYWORD_PRIORITY));
        }
    }
}
