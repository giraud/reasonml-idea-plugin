package com.reason.hints;

import com.intellij.openapi.vfs.*;
import com.reason.ide.hints.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.nio.file.*;
import java.util.*;

public interface InsightManager {
    void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile);

    void queryTypes(@NotNull VirtualFile sourceFile, @NotNull Path path, @NotNull ORProcessTerminated<InferredTypes> callback);

    @NotNull
    List<String> dumpMeta(@NotNull VirtualFile cmtFile);

    @NotNull
    String dumpTree(@NotNull VirtualFile cmtFile);

    @NotNull
    List<String> dumpInferredTypes(@NotNull VirtualFile cmtFile);
}
