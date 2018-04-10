package com.reason.bs.annotations;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.ConcurrentMultiMap;

import java.util.Collection;

public class BsErrorsManagerImpl extends BsErrorsManager {

    private ConcurrentMultiMap<String, BsbError> m_errorsByFile = new ConcurrentMultiMap<>();

    @Override
    public void setError(String filePath, BsbError error) {
        VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
        if (fileByUrl != null) {
            m_errorsByFile.putValue(fileByUrl.getCanonicalPath(), error);
        }
    }

    @Override
    public Collection<BsbError> getErrors(String filePath) {
        return m_errorsByFile.get(filePath);
    }

    @Override
    public void clearErrors() {
        m_errorsByFile.clear();
    }

}
