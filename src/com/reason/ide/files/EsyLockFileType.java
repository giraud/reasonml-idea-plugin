package com.reason.ide.files;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EsyLockFileType implements FileType {

    public static final FileType INSTANCE = new EsyLockFileType();

    private EsyLockFileType() {}

    @NotNull
    @Override
    public String getName() {
        return "Esy Lock File";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getDescription() {
        return "Esy lock file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "lock";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AllIcons.Nodes.Padlock;
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
