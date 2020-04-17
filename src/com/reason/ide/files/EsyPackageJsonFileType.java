package com.reason.ide.files;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.reason.Icons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
        return Icons.ESY_FILE;
    }
}
