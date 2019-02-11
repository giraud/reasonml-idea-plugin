package com.reason.ide.search;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

public interface IndexedFileModule {
    String getNamespace();

    String getModuleName();

    boolean isOCaml();

    boolean isInterface();

    boolean isComponent();

    String getPath();

    @Nullable
    VirtualFile getVirtualFile();
}
