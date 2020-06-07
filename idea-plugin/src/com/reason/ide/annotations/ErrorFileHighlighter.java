package com.reason.ide.annotations;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.files.FileHelper;
import org.jetbrains.annotations.NotNull;

public class ErrorFileHighlighter implements Condition<VirtualFile> {
    @Override
    public boolean value(@NotNull VirtualFile file) {
        FileType fileType = file.getFileType();
        return FileHelper.isOCaml(fileType) || FileHelper.isReason(fileType);
    }
}
