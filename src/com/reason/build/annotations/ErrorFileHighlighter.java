package com.reason.build.annotations;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.files.FileHelper;

public class ErrorFileHighlighter implements Condition<VirtualFile> {
    @Override
    public boolean value(VirtualFile file) {
        FileType fileType = file.getFileType();
        return FileHelper.isOCaml(fileType) || FileHelper.isReason(fileType);
    }
}
