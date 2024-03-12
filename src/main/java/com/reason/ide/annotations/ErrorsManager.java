package com.reason.ide.annotations;

import com.intellij.openapi.components.*;
import com.intellij.util.containers.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.HashMap;
import java.util.*;
import java.util.concurrent.*;

@Service(Service.Level.PROJECT)
public final class ErrorsManager {
    private static final Log LOG = Log.create("errors");

    private final Map<String, Map<Integer, OutputInfo>> m_errorsByFile = new ConcurrentHashMap<>();
    private final MultiMap<String, OutputInfo> m_warningsByFile = MultiMap.createConcurrent();

    public void addAllInfo(@NotNull Collection<OutputInfo> bsbInfo) {
        LOG.debug("Adding errors/warnings", bsbInfo);

        for (OutputInfo info : bsbInfo) {
            LOG.trace("  -> " + bsbInfo);
            if (info != null && info.path != null && !info.path.isEmpty()) {
                if (info.isError) {
                    m_errorsByFile.compute(
                            info.path,
                            (k, v) -> {
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

    public void clearErrors() {
        m_errorsByFile.clear();
        m_warningsByFile.clear();
    }

}
