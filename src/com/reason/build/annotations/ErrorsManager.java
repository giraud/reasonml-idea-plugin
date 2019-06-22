package com.reason.build.annotations;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ConcurrentMultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ErrorsManager {

    void addAllInfo(@NotNull Iterable<OutputInfo> bsbInfo);

    @NotNull
    Collection<OutputInfo> getInfo(@NotNull String filePath);

    @NotNull
    ConcurrentMultiMap<String, OutputInfo> getAllErrors();

    boolean hasErrors(@NotNull VirtualFile file);

    void clearErrors();
}
