package com.reason.ide.repl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Handle the prompt commands history.
 */
final class PromptHistory {
    private final int m_maxSize;
    private final List<String> m_history = new LinkedList<>();
    private int m_historyIndex = 0;

    PromptHistory(int maxSize) {
        m_maxSize = maxSize;
    }

    void addInHistory(@NotNull String command) {
        if (!command.trim().isEmpty()) {
            m_history.remove(command);
            while (m_history.size() >= m_maxSize) {
                m_history.remove(0);
            }
            m_history.add(command);
        }
        m_historyIndex = m_history.size();
    }

    @Nullable
    String getFromHistory(boolean next) {
        if (next) {
            if ((m_historyIndex + 1) < m_history.size()) {
                m_historyIndex++;
                return m_history.get(m_historyIndex);
            } else {
                m_historyIndex = m_history.size();
                return "";
            }
        } else {
            if (m_historyIndex > 0) {
                m_historyIndex--;
                return m_history.get(m_historyIndex);
            } else {
                return null;
            }
        }
    }
}
