package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class CmtFileType implements FileType {
    public static final CmtFileType INSTANCE = new CmtFileType();

    @NotNull
    @Override
    public String getName() {
        return "CMT";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Cmt";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "cmt";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return UnknownFileType.INSTANCE.getIcon();
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public @Nullable String getCharset(@NotNull VirtualFile file, byte @NotNull [] content) {
        return null;
    }
}
