package com.reason.build.annotations;

import com.intellij.util.containers.ConcurrentMultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ErrorsManager {

    void put(@Nullable OutputInfo error);

    void addAllInfo(@NotNull Iterable<OutputInfo> bsbInfo);

    @NotNull
    Collection<OutputInfo> getErrors(@NotNull String filePath);

    @NotNull
    ConcurrentMultiMap<String, OutputInfo> getAllErrors();

    void clearErrors();
}
