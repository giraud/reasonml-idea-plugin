package com.reason.ide.files;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import icons.ORIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.reason.esy.EsyConstants.ESY_CONFIG_FILENAME;

public class EsyPackageJsonFileType extends JsonFileType {

    public static final FileType INSTANCE = new EsyPackageJsonFileType();

    public static String getDefaultFilename() {
        return ESY_CONFIG_FILENAME;
    }

    private EsyPackageJsonFileType() {
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ORIcons.ESY_FILE;
    }
}
