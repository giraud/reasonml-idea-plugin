package com.reason.hints;

import com.intellij.openapi.vfs.*;
import com.reason.ide.hints.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public interface InsightManager {

  @FunctionalInterface
  interface ProcessTerminated {
    void run(@NotNull InferredTypes types);
  }

  void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile);

  @Nullable
  File getRincewindFile(@NotNull VirtualFile sourceFile);

  @Nullable
  String getRincewindFilename(@NotNull VirtualFile sourceFile);

  void queryTypes(@NotNull VirtualFile sourceFile, @NotNull Path path, @NotNull ProcessTerminated callback);

  @NotNull
  List<String> dumpMeta(@NotNull VirtualFile cmtFile);

  @NotNull
  String dumpTree(@NotNull VirtualFile cmtFile);

  @NotNull
  List<String> dumpInferredTypes(@NotNull VirtualFile cmtFile);
}
