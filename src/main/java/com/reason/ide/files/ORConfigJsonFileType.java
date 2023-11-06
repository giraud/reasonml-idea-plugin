package com.reason.ide.files;

import com.intellij.json.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ORConfigJsonFileType extends JsonFileType {
    public static final ORConfigJsonFileType INSTANCE = new ORConfigJsonFileType(); // used in plugin.xml

    @Override
    public @NotNull String getName() {
        return "Compiler configuration";
    }

    @Override
    public @NotNull String getDescription() {
        return "Compiler configuration file";
    }

    @Override
    public @Nullable Icon getIcon() {
        return ORIcons.BUCKLESCRIPT_TOOL;
    }
}
