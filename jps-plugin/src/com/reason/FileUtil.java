package com.reason;

import com.intellij.openapi.vfs.VirtualFile;
import java.io.*;
import org.jetbrains.annotations.NotNull;

public class FileUtil {
  public static String readFileContent(@NotNull VirtualFile file) {
    try {
      return Streams.inputToString(file.getInputStream());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
