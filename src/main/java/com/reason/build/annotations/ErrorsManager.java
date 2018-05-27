package com.reason.build.annotations;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;

public interface ErrorsManager {

    void put(@Nullable OutputInfo error);

    void addAllInfo(@NotNull Iterable<OutputInfo> bsbInfo);

    @NotNull
    Collection<OutputInfo> getErrors(@NotNull String filePath);

    void clearErrors();

}
