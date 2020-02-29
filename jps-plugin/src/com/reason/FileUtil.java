package com.reason;

import java.io.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.vfs.VirtualFile;

public class FileUtil {
    public static String readFileContent(@NotNull VirtualFile file) {
        try {
            return Streams.inputToString(file.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
