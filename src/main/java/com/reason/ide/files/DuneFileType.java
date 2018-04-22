package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DuneFileType  implements FileType {
    public static final FileType INSTANCE = new DuneFileType();

    @NotNull
    @Override
    public String getName() {
        return "Dune configuration";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Dune (jbuilder) configuration file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.DUNE_FILE;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return null;
    }
}
