package com.reason.ide.annotations;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ErrorsManager {

    void addAllInfo(@NotNull Collection<OutputInfo> bsbInfo);

    @NotNull
    Collection<OutputInfo> getInfo(@NotNull String moduleName);

    boolean hasErrors(@NotNull String moduleName, int lineNumber);

    void clearErrors();

    void clearErrors(@NotNull String moduleName);
}
