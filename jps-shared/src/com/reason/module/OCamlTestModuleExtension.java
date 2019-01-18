package com.reason.module;

import org.jetbrains.annotations.Nullable;

public interface OCamlTestModuleExtension {
    void setLevel(final String languageLevel);

    @Nullable
    String getLevel();
}
