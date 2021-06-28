package com.reason;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class AutoDeletingTempFile implements AutoCloseable {
    private final File myFile;

    public AutoDeletingTempFile(@NotNull String prefix, @NotNull String extension) throws IOException {
        myFile = FileUtilRt.createTempFile(prefix, extension, true);
    }

    public String getPath() {
        return myFile.getPath();
    }

    public void write(String text) throws IOException {
        FileUtil.writeToFile(myFile, text.getBytes());
    }

    @Override public void close() {
        FileUtilRt.delete(myFile);
    }
}
