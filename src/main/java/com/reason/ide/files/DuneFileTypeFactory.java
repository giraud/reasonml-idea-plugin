package com.reason.ide.files;

import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class DuneFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(DuneFileType.INSTANCE, new ExactFileNameMatcher("jbuild"));
    }
}
