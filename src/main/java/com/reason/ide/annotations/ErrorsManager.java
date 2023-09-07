package com.reason.ide.annotations;

import org.jetbrains.annotations.*;

import java.util.*;

public interface ErrorsManager {
    void addAllInfo(@NotNull Collection<OutputInfo> bsbInfo);

    void clearErrors();
}
