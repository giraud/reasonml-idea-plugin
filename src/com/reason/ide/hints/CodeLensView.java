package com.reason.ide.hints;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ObjectLongHashMap;
import gnu.trove.THashMap;
import gnu.trove.TObjectLongHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CodeLensView {
    public static final Key<CodeLensInfo> CODE_LENS = Key.create("reasonml.codelens");

    private CodeLensView() {
    }

    public static class CodeLensInfo {
        Map<VirtualFile, Map<Integer, String>> m_signatures = new THashMap<>();
        private final TObjectLongHashMap<VirtualFile> m_timestamps = new ObjectLongHashMap<>();

        @Nullable
        public synchronized String get(@NotNull VirtualFile file, int line, long currentTimestamp) {
            long timestamp = m_timestamps.get(file);
            if (timestamp != -1 && timestamp < currentTimestamp) {
                return null;
            }

            Map<Integer, String> integerStringMap = m_signatures.get(file);
            if (integerStringMap == null) {
                return null;
            }

            return integerStringMap.get(line);
        }

        synchronized void put(@NotNull VirtualFile file, @NotNull LogicalPosition position, @NotNull String signature/*Map of sig?*/, long timestamp) {
            m_timestamps.put(file, timestamp);
            Map<Integer, String> integerStringMap = m_signatures.get(file);
            if (integerStringMap == null) {
                integerStringMap = new THashMap<>();
                m_signatures.put(file, integerStringMap);
            }
            integerStringMap.putIfAbsent(position.line, signature);
        }

        public synchronized void move(@NotNull VirtualFile file, int startLine, int direction, long timestamp) {
            m_timestamps.put(file, timestamp);
            Map<Integer, String> signaturesByLine = m_signatures.get(file);
            if (signaturesByLine != null) {
                Map<Integer, String> signatures = new THashMap<>();
                for (Map.Entry<Integer, String> signatureEntry : signaturesByLine.entrySet()) {
                    Integer line = signatureEntry.getKey();
                    signatures.put((startLine <= line) ? line + direction : line, signatureEntry.getValue());
                }
                m_signatures.put(file, signatures);
            }
        }

        synchronized void clearInternalData(@NotNull VirtualFile virtualFile) {
            m_timestamps.remove(virtualFile);
            Map<Integer, String> integerStringMap = m_signatures.get(virtualFile);
            if (integerStringMap != null) {
                integerStringMap.clear();
            }
        }
    }

}
