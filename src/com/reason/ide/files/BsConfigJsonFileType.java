package com.reason.ide.files;

import com.intellij.json.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class BsConfigJsonFileType extends JsonFileType {
    public static final BsConfigJsonFileType INSTANCE = new BsConfigJsonFileType();

    public static boolean isBsConfigFile(@NotNull VirtualFile file) {
        return ORConstants.BS_CONFIG_FILENAME.equals(file.getName());
    }

    @Override
    public @NotNull String getName() {
        return "BuckleScript Configuration";
    }

    @Override
    public @NotNull String getDescription() {
        return "BuckleScript configuration file";
    }

    @Override
    public @Nullable Icon getIcon() {
        return ORIcons.BUCKLESCRIPT_TOOL;
    }
}
