package com.reason.ide.files;

import com.intellij.framework.detection.FileContentPattern;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.FileViewProvider;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.FileContentImpl;
import org.jetbrains.annotations.NotNull;

public class BsConfigJsonFile extends FileBase {

    public static boolean isBsConfigJson(@NotNull VirtualFile virtualFile) {
        FileContent fileContent = FileContentImpl.createByFile(virtualFile);
        return createFilePattern().accepts(fileContent);
    }

    BsConfigJsonFile(@NotNull FileViewProvider viewProvider, @NotNull Language language) {
        super(viewProvider, language);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return BsConfigJsonFileType.INSTANCE;
    }

    private static ElementPattern<FileContent> createFilePattern() {
        return FileContentPattern.fileContent()
                .withName(BsConfigJsonFileType.getDefaultFilename());
    }
}
