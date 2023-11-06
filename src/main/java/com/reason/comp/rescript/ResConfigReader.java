package com.reason.comp.rescript;

import com.intellij.openapi.vfs.*;
import com.reason.comp.bs.*;
import org.jetbrains.annotations.*;

import java.util.regex.*;

public class ResConfigReader {
    private ResConfigReader() {
    }

    public static @NotNull BsConfig read(@NotNull VirtualFile bsConfigFile) {
        return read(bsConfigFile, false);
    }

    public static @NotNull BsConfig read(@NotNull VirtualFile configFile, boolean useExternalAsSource) {
        // For now, the same than bsconfig
        return BsConfigReader.read(configFile, useExternalAsSource);
    }
}
