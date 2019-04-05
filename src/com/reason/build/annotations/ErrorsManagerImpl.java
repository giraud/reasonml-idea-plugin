package com.reason.build.annotations;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.ConcurrentMultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ErrorsManagerImpl implements ErrorsManager, ProjectComponent {

    private final ConcurrentMultiMap<String, OutputInfo> m_errorsByFile = new ConcurrentMultiMap<>();

    @Override
    public void put(@Nullable OutputInfo info) {
        if (info != null && !info.path.isEmpty()) {
            VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl("file://" + info.path);
            if (fileByUrl != null) {
                m_errorsByFile.putValue(fileByUrl.getPath(), info);
            }
        }
    }

    @Override
    public void addAllInfo(@NotNull Iterable<OutputInfo> bsbInfo) {
        for (OutputInfo info : bsbInfo) {
            put(info);
        }
    }

    @NotNull
    @Override
    public Collection<OutputInfo> getErrors(@NotNull String filePath) {
        return m_errorsByFile.get(filePath);
    }

    @NotNull
    @Override
    public ConcurrentMultiMap<String, OutputInfo> getAllErrors() {
        return m_errorsByFile;
    }

    @Override
    public void clearErrors() {
        m_errorsByFile.clear();
    }

    //region Compatibility
    @Override
    public void initComponent() { // For compatibility with idea#143
    }

    @Override
    public void disposeComponent() { // For compatibility with idea#143
    }
    //endregion
}
