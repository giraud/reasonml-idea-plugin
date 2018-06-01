package com.reason.build.annotations;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ErrorsManager {

    void put(@Nullable OutputInfo error);

    void addAllInfo(@NotNull Iterable<OutputInfo> bsbInfo);

    @NotNull
    Collection<OutputInfo> getErrors(@NotNull String filePath);

    void clearErrors();
}
