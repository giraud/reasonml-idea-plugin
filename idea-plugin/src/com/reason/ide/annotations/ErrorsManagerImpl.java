package com.reason.ide.annotations;

import com.intellij.util.containers.ConcurrentMultiMap;
import com.reason.Joiner;
import com.reason.Log;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ErrorsManagerImpl implements ErrorsManager {

    private final static Log LOG = Log.create("errors");

    private final ConcurrentMap<String, Map<Integer, OutputInfo>> m_errorsByFile = new ConcurrentHashMap<>();
    private final ConcurrentMultiMap<String, OutputInfo> m_warningsByFile = new ConcurrentMultiMap<>();

    @Override
    public void addAllInfo(@NotNull Collection<OutputInfo> bsbInfo) {
        LOG.debug("Adding errors/warnings", bsbInfo);

        for (OutputInfo info : bsbInfo) {
            LOG.trace("  -> " + bsbInfo);
            if (info != null && info.path != null && !info.path.isEmpty()) {
                if (info.isError) {
                    m_errorsByFile.compute(info.path, (k, v) -> {
                        if (v == null) {
                            Map<Integer, OutputInfo> byLine = new HashMap<>();
                            byLine.put(info.lineStart, info);
                            return byLine;
                        }
                        v.put(info.lineStart, info);
                        return v;
                    });
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("  -> error: " + info);
                    }
                } else {
                    m_warningsByFile.putValue(info.path, info);
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("  -> warning: " + info);
                    }
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("  -> errors:   [" + Joiner.join(", ", m_errorsByFile.keySet()) + "]");
            LOG.debug("  -> warnings: [" + Joiner.join(", ", m_warningsByFile.keySet()) + "]");
        }
    }

    @NotNull
    @Override
    public Collection<OutputInfo> getInfo(@NotNull String moduleName) {
        Map<Integer, OutputInfo> infoMap = m_errorsByFile.get(moduleName);
        ArrayList<OutputInfo> result = infoMap == null ? new ArrayList<>() : new ArrayList<>(infoMap.values());
        result.addAll(m_warningsByFile.get(moduleName));
        if (LOG.isTraceEnabled()) {
            LOG.trace("getInfo for " + moduleName + ": [" + Joiner.join(", ", result) + "]");
        }
        return result;
    }

    @Override
    public boolean hasErrors(@NotNull String moduleName, int lineNumber) {
        Map<Integer, OutputInfo> infoMap = m_errorsByFile.get(moduleName);
        return infoMap != null && infoMap.containsKey(lineNumber);
    }

    public void clearErrors() {
        m_errorsByFile.clear();
        m_warningsByFile.clear();
    }

    @Override
    public void clearErrors(@NotNull String moduleName) {
        m_errorsByFile.remove(moduleName);
        m_warningsByFile.remove(moduleName);
    }
}
