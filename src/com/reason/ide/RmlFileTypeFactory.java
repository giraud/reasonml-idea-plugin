package com.reason.ide;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class RmlFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(RmlFileType.INSTANCE, "re");
    }
}
