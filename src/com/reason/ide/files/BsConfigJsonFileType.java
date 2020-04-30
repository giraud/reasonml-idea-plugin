package com.reason.ide.files;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;
import icons.ORIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BsConfigJsonFileType extends JsonFileType {

    public static final BsConfigJsonFileType INSTANCE = new BsConfigJsonFileType();

    public static String getDefaultFilename() {
        return "bsconfig.json";
    }

    public static boolean isBsConfigFile(VirtualFile file) {
        return getDefaultFilename().equals(file.getName());
    }

    @NotNull
    @Override
    public String getName() {
        return "BuckleScript Configuration";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "BuckleScript configuration file";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ORIcons.BUCKLESCRIPT_TOOL;
    }
}
