package com.reason.ide.files;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.BinaryFileDecompiler;
import com.intellij.openapi.vfs.VirtualFile;

public class CmtFileDecompiler implements BinaryFileDecompiler {
    @NotNull
    @Override
    public CharSequence decompile(@NotNull VirtualFile file) {
        return "<file name=\"" + file.getName() + "\"><test/></file>";
    }
}
