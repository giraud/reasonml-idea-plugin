package com.reason.bs;

import com.intellij.framework.detection.FileContentPattern;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.FileContentImpl;
import com.reason.ide.files.BsConfigJsonFileType;
import org.jetbrains.annotations.NotNull;

public class BsConfigJson {

    private BsConfigJson() {}

    public static boolean isBsConfigJson(@NotNull VirtualFile virtualFile) {
        FileContent fileContent = FileContentImpl.createByFile(virtualFile);
        return createFilePattern().accepts(fileContent);
    }

    public static FileType getFileType() {
        return BsConfigJsonFileType.INSTANCE;
    }

    private static ElementPattern<FileContent> createFilePattern() {
        return FileContentPattern.fileContent()
                .withName(BsConfigJsonFileType.getDefaultFilename());
    }
}
