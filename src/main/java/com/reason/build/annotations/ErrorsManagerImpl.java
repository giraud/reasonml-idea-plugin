package com.reason.build.annotations;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.ConcurrentMultiMap;

import javax.annotation.Nullable;
import java.util.Collection;

public class ErrorsManagerImpl implements ErrorsManager {

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
    public Collection<OutputInfo> getErrors(String filePath) {
        return m_errorsByFile.get(filePath);
    }

    @Override
    public void clearErrors() {
        m_errorsByFile.clear();
    }

}
