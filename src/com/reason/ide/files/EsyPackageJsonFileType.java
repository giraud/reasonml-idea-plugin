package com.reason.ide.files;

import javax.swing.*;
import org.jetbrains.annotations.Nullable;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import icons.ORIcons;

public class EsyPackageJsonFileType extends JsonFileType {

    public static final FileType INSTANCE = new EsyPackageJsonFileType();

    public static String getDefaultFilename() {
        return "package.json";
    }

    private EsyPackageJsonFileType() {
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ORIcons.ESY_FILE;
    }
}
