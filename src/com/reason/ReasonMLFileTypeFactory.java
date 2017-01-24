package com.reason;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class ReasonMLFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(ReasonMLFileType.INSTANCE, "re");
    }
}
