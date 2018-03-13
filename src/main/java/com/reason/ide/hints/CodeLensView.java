package com.reason.ide.hints;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ObjectLongHashMap;
import gnu.trove.THashMap;
import gnu.trove.TObjectLongHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CodeLensView {
    public static final Key<CodeLensInfo> CODE_LENS = Key.create("reasonml.codelens");

    public static class CodeLensInfo {
        Map<Pair<VirtualFile, Integer>, String> m_signatures = new THashMap<>();
        private final TObjectLongHashMap<VirtualFile> m_timestamps = new ObjectLongHashMap<>();

        @Nullable
        public synchronized String get(@NotNull VirtualFile file, int line, long currentTimestamp) {
            long timestamp = m_timestamps.get(file);
            if (timestamp == -1 || timestamp < currentTimestamp) {
                return null;
            }

            return m_signatures.get(Pair.create(file, line));
        }

        public synchronized void put(@NotNull VirtualFile file, @NotNull LogicalPosition position, @NotNull String signature/*Map of sig?*/, long timestamp) {
            m_timestamps.put(file, timestamp);
            Pair<VirtualFile, Integer> key = Pair.create(file, position.line);
            m_signatures.putIfAbsent(key, signature);
        }

        public synchronized void clearInternalData() {
            m_timestamps.clear();
            m_signatures.clear();
        }
    }

}
