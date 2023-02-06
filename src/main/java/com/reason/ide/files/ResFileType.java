package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.fileTypes.ex.*;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.newvfs.*;
import com.reason.ide.*;
import com.reason.lang.rescript.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ResFileType extends LanguageFileType implements FileTypeIdentifiableByVirtualFile {
    public static final ResFileType INSTANCE = new ResFileType();
    private static final Log LOG = Log.create("fileType");

    private ResFileType() {
        super(ResLanguage.INSTANCE);
    }

    @Override
    public boolean isMyFileType(@NotNull VirtualFile file) {
        if (!file.isDirectory() && "res".equals(file.getExtension())) {
            // must protect from .res resources files found in jar files
            VirtualFileSystem entryFileSystem = file.getFileSystem();
            boolean isArchive = entryFileSystem instanceof ArchiveFileSystem;
            if (LOG.isTraceEnabled()) {
                LOG.trace("Testing entry file: " + entryFileSystem + ", archive? " + isArchive + " (" + file.getPath() + "/" + file.getName() + ")");
            }
            return !isArchive;
        }
        return false;
    }

    @Override
    public @NotNull String getName() {
        return "RESCRIPT";
    }

    @Override
    public @NotNull String getDescription() {
        return "Rescript language file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return ""; // Can't define an extension, use isMyFileType instead
    }

    @Override
    public @NotNull Icon getIcon() {
        return ORIcons.NS_FILE;
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }
}
