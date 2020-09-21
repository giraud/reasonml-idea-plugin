package com.reason.hints;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.hints.InferredTypes;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InsightManager {

  @FunctionalInterface
  interface ProcessTerminated {
    void run(InferredTypes types);
  }

  void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile);

  @Nullable
  File getRincewindFile(@NotNull VirtualFile sourceFile);

  @Nullable
  String getRincewindFilename(@NotNull VirtualFile sourceFile);

  void queryTypes(
      @NotNull VirtualFile sourceFile, @NotNull Path path, @NotNull ProcessTerminated callback);

  @NotNull
  List<String> dumpMeta(@NotNull VirtualFile cmtFile);

  @NotNull
  String dumpTree(@NotNull VirtualFile cmtFile);

  @NotNull
  List<String> dumpInferredTypes(@NotNull VirtualFile cmtFile);
}
