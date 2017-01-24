package com.reason.ide;

import com.intellij.openapi.fileTypes.*;
import com.reason.ide.ReasonMLFileType;
import org.jetbrains.annotations.NotNull;

public class ReasonMLFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(ReasonMLFileType.INSTANCE, "re");
    }
}
