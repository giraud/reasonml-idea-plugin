package com.reason.ide.search;

import org.jetbrains.annotations.NotNull;

public interface IndexedFileModule {
    @NotNull
    String getNamespace();

    @NotNull
    String getModuleName();

    @NotNull
    String getPath();

    @NotNull
    String getFullname();

    boolean isOCaml();

    boolean isInterface();

    boolean isComponent();
}
