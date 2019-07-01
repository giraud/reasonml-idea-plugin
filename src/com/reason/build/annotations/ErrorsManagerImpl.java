package com.reason.build.annotations;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.ConcurrentMultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ErrorsManagerImpl implements ErrorsManager {

    private final ConcurrentMultiMap<String, OutputInfo> m_errorsByFile = new ConcurrentMultiMap<>();
    private final ConcurrentMultiMap<String, OutputInfo> m_warningsByFile = new ConcurrentMultiMap<>();

    @Override
    public void addAllInfo(@NotNull Iterable<OutputInfo> bsbInfo) {
        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        for (OutputInfo info : bsbInfo) {
            if (info != null && info.path != null && !info.path.isEmpty()) {
                VirtualFile fileByUrl = virtualFileManager.findFileByUrl("file://" + info.path);
                if (fileByUrl != null) {
                    if (info.isError) {
                        m_errorsByFile.putValue(fileByUrl.getPath(), info);
                    } else {
                        m_warningsByFile.putValue(fileByUrl.getPath(), info);
                    }
                }
            }
        }
    }

    @NotNull
    @Override
    public Collection<OutputInfo> getInfo(@NotNull String filePath) {
        ArrayList<OutputInfo> result = new ArrayList<>(m_errorsByFile.get(filePath));
        result.addAll(m_warningsByFile.get(filePath));
        return result;
    }

    @NotNull
    @Override
    public ConcurrentMultiMap<String, OutputInfo> getAllErrors() {
        return m_errorsByFile;
    }

    @Override
    public boolean hasErrors(@NotNull VirtualFile file) {
        String filePath = file.getCanonicalPath();
        return filePath != null && m_errorsByFile.containsKey(filePath);
    }

    @Override
    public void clearErrors() {
        m_errorsByFile.clear();
        m_warningsByFile.clear();
    }
}
