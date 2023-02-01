package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.fileTypes.ex.*;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.newvfs.*;
import com.reason.ide.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ResFileType extends LanguageFileType implements FileTypeIdentifiableByVirtualFile {
    public static final ResFileType INSTANCE = new ResFileType();
    //private static final Log LOG = Log.create("fileType");

    private ResFileType() {
        super(ResLanguage.INSTANCE);
    }

    @Override public boolean isMyFileType(@NotNull VirtualFile file) {
        if (!file.isDirectory() && "res".equals(file.getExtension())) {
            // must protect from resources .res files found in jar files
            VirtualFileSystem entryFileSystem = file.getFileSystem();
            return !(entryFileSystem instanceof ArchiveFileSystem);
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
        return "";
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
