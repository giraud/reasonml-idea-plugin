package com.reason.ide.files;

import com.google.common.collect.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.vfs.*;
import com.reason.lang.dune.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

import static com.reason.comp.dune.DunePlatform.*;

public class DuneFileType extends LanguageFileType {

    public static final FileType INSTANCE = new DuneFileType();

    public static Set<String> getDefaultFilenames() {
        return ImmutableSet.of(DUNE_FILENAME, DUNE_PROJECT_FILENAME, LEGACY_JBUILDER_FILENAME);
    }

    public static boolean isDuneFile(@NotNull VirtualFile file) {
        return getDefaultFilenames().stream().anyMatch(filename -> filename.equals(file.getName()));
    }

    private DuneFileType() {
        super(DuneLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "DUNE";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Dune configuration file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ORIcons.DUNE_FILE;
    }
}
